package de.braegel.uvgard.data.api

import de.braegel.uvgard.data.model.UvData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CurrentUvIndexParserTest {

    private val sampleResponse = """
    {
      "ok": true,
      "latitude": 52.52,
      "longitude": 13.41,
      "now": {
        "time": "2025-07-15T12:00:00Z",
        "uvi": 5.2
      },
      "forecast": [
        { "time": "2025-07-15T13:00:00Z", "uvi": 6.1 },
        { "time": "2025-07-15T14:00:00Z", "uvi": 7.8 },
        { "time": "2025-07-15T15:00:00Z", "uvi": 6.5 },
        { "time": "2025-07-15T16:00:00Z", "uvi": 4.2 }
      ]
    }
    """.trimIndent()

    @Test
    fun `parses current uv index from response`() {
        val result: UvData = CurrentUvIndexParser.parse(sampleResponse)
        assertEquals(5.2, result.currentUvIndex, 0.01)
    }

    @Test
    fun `computes daily max uv index from forecast`() {
        val result: UvData = CurrentUvIndexParser.parse(sampleResponse)
        assertEquals(7.8, result.dailyMaxUvIndex, 0.01)
    }

    @Test
    fun `parses coordinates from response`() {
        val result: UvData = CurrentUvIndexParser.parse(sampleResponse)
        assertEquals(52.52, result.latitude, 0.01)
        assertEquals(13.41, result.longitude, 0.01)
    }

    @Test
    fun `parses timestamp from response`() {
        val result: UvData = CurrentUvIndexParser.parse(sampleResponse)
        assertNotNull(result.timestamp)
    }

    @Test
    fun `daily max includes current value in comparison`() {
        val responseWithHigherNow = """
        {
          "ok": true,
          "latitude": 52.52,
          "longitude": 13.41,
          "now": {
            "time": "2025-07-15T12:00:00Z",
            "uvi": 9.0
          },
          "forecast": [
            { "time": "2025-07-15T13:00:00Z", "uvi": 6.1 },
            { "time": "2025-07-15T14:00:00Z", "uvi": 7.8 }
          ]
        }
        """.trimIndent()
        val result: UvData = CurrentUvIndexParser.parse(responseWithHigherNow)
        assertEquals(9.0, result.dailyMaxUvIndex, 0.01)
    }
}