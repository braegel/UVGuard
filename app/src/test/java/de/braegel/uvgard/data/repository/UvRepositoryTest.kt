package de.braegel.uvgard.data.repository

import de.braegel.uvgard.data.model.UvData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UvRepositoryTest {

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

    private val currentUvIndexResponse = """
    {
      "ok": true,
      "latitude": 52.52,
      "longitude": 13.41,
      "now": {
        "time": "2025-07-15T12:00:00Z",
        "uvi": 5.0
      },
      "forecast": [
        { "time": "2025-07-15T13:00:00Z", "uvi": 6.1 },
        { "time": "2025-07-15T14:00:00Z", "uvi": 7.5 }
      ]
    }
    """.trimIndent()

    private fun fakeHttpClient(responses: Map<String, String>): UvHttpClient {
        return object : UvHttpClient {
            override suspend fun get(url: String): String {
                return responses[url] ?: throw Exception("No fake response for $url")
            }
        }
    }

    @Test
    fun `fetches uv data from open-meteo`() = runTest {
        val client = fakeHttpClient(mapOf(
            "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=uv_index&daily=uv_index_max&timezone=auto" to openMeteoResponse
        ))
        val repo = UvRepository(client)
        val result = repo.fetchFromOpenMeteo(52.52, 13.41)

        assertEquals(5.2, result.currentUvIndex, 0.01)
        assertEquals(7.8, result.dailyMaxUvIndex, 0.01)
    }

    @Test
    fun `fetches uv data from currentuvindex`() = runTest {
        val client = fakeHttpClient(mapOf(
            "https://currentuvindex.com/api/v1/uvi?latitude=52.52&longitude=13.41" to currentUvIndexResponse
        ))
        val repo = UvRepository(client)
        val result = repo.fetchFromCurrentUvIndex(52.52, 13.41)

        assertEquals(5.0, result.currentUvIndex, 0.01)
        assertEquals(7.5, result.dailyMaxUvIndex, 0.01)
    }

    @Test
    fun `fetch returns first successful result`() = runTest {
        val client = fakeHttpClient(mapOf(
            "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=uv_index&daily=uv_index_max&timezone=auto" to openMeteoResponse
        ))
        val repo = UvRepository(client)
        val result = repo.fetch(52.52, 13.41)

        assertNotNull(result)
        assertEquals(52.52, result!!.latitude, 0.01)
    }

    @Test
    fun `fetch falls back to second api if first fails`() = runTest {
        val client = fakeHttpClient(mapOf(
            "https://currentuvindex.com/api/v1/uvi?latitude=52.52&longitude=13.41" to currentUvIndexResponse
        ))
        val repo = UvRepository(client)
        val result = repo.fetch(52.52, 13.41)

        assertNotNull(result)
        assertEquals(5.0, result!!.currentUvIndex, 0.01)
    }

    @Test
    fun `fetch returns null if all apis fail`() = runTest {
        val client = fakeHttpClient(emptyMap())
        val repo = UvRepository(client)
        val result = repo.fetch(52.52, 13.41)

        assertTrue(result == null)
    }
}
