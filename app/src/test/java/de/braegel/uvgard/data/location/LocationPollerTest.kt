package de.braegel.uvgard.data.location

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationPollerTest {

    private var requestCount = 0

    private val fakeLocationSource = object : LocationSource {
        override suspend fun getLocation(): LocationData {
            requestCount++
            return LocationData(latitude = 52.52, longitude = 13.41)
        }
    }

    @Test
    fun `emits location immediately on start`() = runTest {
        val poller = LocationPoller(fakeLocationSource, intervalMs = 10 * 60 * 1000L)
        val results = mutableListOf<LocationData>()

        val job = launch { poller.locationFlow().toList(results) }
        advanceTimeBy(1)

        assertEquals(1, results.size)
        assertEquals(52.52, results[0].latitude, 0.01)
        assertEquals(13.41, results[0].longitude, 0.01)

        job.cancel()
    }

    @Test
    fun `emits location again after 10 minutes`() = runTest {
        val poller = LocationPoller(fakeLocationSource, intervalMs = 10 * 60 * 1000L)
        val results = mutableListOf<LocationData>()

        val job = launch { poller.locationFlow().toList(results) }
        advanceTimeBy(10 * 60 * 1000L + 1)

        assertEquals(2, results.size)

        job.cancel()
    }

    @Test
    fun `emits three locations after 20 minutes`() = runTest {
        val poller = LocationPoller(fakeLocationSource, intervalMs = 10 * 60 * 1000L)
        val results = mutableListOf<LocationData>()

        val job = launch { poller.locationFlow().toList(results) }
        advanceTimeBy(20 * 60 * 1000L + 1)

        assertEquals(3, results.size)

        job.cancel()
    }

    @Test
    fun `does not emit between intervals`() = runTest {
        val poller = LocationPoller(fakeLocationSource, intervalMs = 10 * 60 * 1000L)
        val results = mutableListOf<LocationData>()

        val job = launch { poller.locationFlow().toList(results) }
        advanceTimeBy(5 * 60 * 1000L)

        assertEquals(1, results.size) // only the initial emission

        job.cancel()
    }

    @Test
    fun `uses provided location source for each poll`() = runTest {
        requestCount = 0
        val poller = LocationPoller(fakeLocationSource, intervalMs = 10 * 60 * 1000L)
        val results = mutableListOf<LocationData>()

        val job = launch { poller.locationFlow().toList(results) }
        advanceTimeBy(20 * 60 * 1000L + 1)

        assertEquals(3, requestCount)

        job.cancel()
    }
}