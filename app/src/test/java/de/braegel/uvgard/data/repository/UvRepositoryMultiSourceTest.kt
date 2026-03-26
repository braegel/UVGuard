package de.braegel.uvgard.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UvRepositoryMultiSourceTest {

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

    @Test
    fun `fetchAll returns results from both apis`() = runTest {
        val client = object : UvHttpClient {
            override suspend fun get(url: String): String {
                return when {
                    url.contains("open-meteo") -> openMeteoResponse
                    url.contains("currentuvindex") -> currentUvIndexResponse
                    else -> throw Exception("Unknown URL")
                }
            }
        }
        val repo = UvRepository(client)
        val results = repo.fetchAll(52.52, 13.41)

        assertEquals(2, results.size)
        assertEquals("Open-Meteo", results[0].source)
        assertEquals("CurrentUVIndex", results[1].source)
    }

    @Test
    fun `fetchAll returns partial results when one api fails`() = runTest {
        val client = object : UvHttpClient {
            override suspend fun get(url: String): String {
                if (url.contains("open-meteo")) throw Exception("API down")
                return currentUvIndexResponse
            }
        }
        val repo = UvRepository(client)
        val results = repo.fetchAll(52.52, 13.41)

        assertEquals(1, results.size)
        assertEquals("CurrentUVIndex", results[0].source)
    }

    @Test
    fun `fetchAll returns empty list when all apis fail`() = runTest {
        val client = object : UvHttpClient {
            override suspend fun get(url: String): String {
                throw Exception("Network error")
            }
        }
        val repo = UvRepository(client)
        val results = repo.fetchAll(52.52, 13.41)

        assertTrue(results.isEmpty())
    }

    @Test
    fun `fetchAll results contain correct uv values per source`() = runTest {
        val client = object : UvHttpClient {
            override suspend fun get(url: String): String {
                return when {
                    url.contains("open-meteo") -> openMeteoResponse
                    url.contains("currentuvindex") -> currentUvIndexResponse
                    else -> throw Exception("Unknown URL")
                }
            }
        }
        val repo = UvRepository(client)
        val results = repo.fetchAll(52.52, 13.41)

        val openMeteo = results.first { it.source == "Open-Meteo" }
        assertEquals(5.2, openMeteo.currentUvIndex, 0.01)
        assertEquals(7.8, openMeteo.dailyMaxUvIndex, 0.01)

        val currentUv = results.first { it.source == "CurrentUVIndex" }
        assertEquals(5.0, currentUv.currentUvIndex, 0.01)
        assertEquals(7.5, currentUv.dailyMaxUvIndex, 0.01)
    }
}
