package de.braegel.uvgard.ui

import de.braegel.uvgard.data.location.LocationData
import de.braegel.uvgard.data.location.LocationPoller
import de.braegel.uvgard.data.location.LocationSource
import de.braegel.uvgard.data.model.UvData
import de.braegel.uvgard.data.repository.UvHttpClient
import de.braegel.uvgard.data.repository.UvRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UvViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val openMeteoResponse = """
    {
      "latitude": 52.52,
      "longitude": 13.41,
      "current": {
        "time": "2025-07-15T12:00",
        "uv_index": 5.2
      },
      "daily": {
        "time": ["2025-07-15"],
        "uv_index_max": [7.8]
      }
    }
    """.trimIndent()

    private val fakeLocationSource = object : LocationSource {
        override suspend fun getLocation(): LocationData {
            return LocationData(latitude = 52.52, longitude = 13.41)
        }
    }

    private val fakeHttpClient = object : UvHttpClient {
        override suspend fun get(url: String): String {
            return openMeteoResponse
        }
    }

    private val failingHttpClient = object : UvHttpClient {
        override suspend fun get(url: String): String {
            throw Exception("Network error")
        }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is null`() {
        val viewModel = UvViewModel(
            locationPoller = LocationPoller(fakeLocationSource, intervalMs = 10 * 60 * 1000L),
            uvRepository = UvRepository(fakeHttpClient)
        )
        assertNull(viewModel.uvData.value)
    }

    @Test
    fun `fetches uv data after start`() = runTest(testDispatcher) {
        val viewModel = UvViewModel(
            locationPoller = LocationPoller(fakeLocationSource, intervalMs = 10 * 60 * 1000L),
            uvRepository = UvRepository(fakeHttpClient),
            scope = backgroundScope
        )
        viewModel.start()
        advanceUntilIdle()

        val result = viewModel.uvData.value
        assertNotNull(result)
        assertTrue(result!!.isNotEmpty())
        assertEquals(5.2, result[0].currentUvIndex, 0.01)
        assertEquals(7.8, result[0].dailyMaxUvIndex, 0.01)
    }

    @Test
    fun `uvData remains null when api fails`() = runTest(testDispatcher) {
        val viewModel = UvViewModel(
            locationPoller = LocationPoller(fakeLocationSource, intervalMs = 10 * 60 * 1000L),
            uvRepository = UvRepository(failingHttpClient),
            scope = backgroundScope
        )
        viewModel.start()
        advanceUntilIdle()

        assertNull(viewModel.uvData.value)
    }
}