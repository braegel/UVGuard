package de.braegel.uvgard.data.api

import de.braegel.uvgard.data.model.UvData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class OpenMeteoParserTest {

    private val sampleResponse = """
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

    @Test
    fun `parses current uv index from response`() {
        val result: UvData = OpenMeteoParser.parse(sampleResponse)
        assertEquals(5.2, result.currentUvIndex, 0.01)
    }

    @Test
    fun `parses daily max uv index from response`() {
        val result: UvData = OpenMeteoParser.parse(sampleResponse)
        assertEquals(7.8, result.dailyMaxUvIndex, 0.01)
    }

    @Test
    fun `parses coordinates from response`() {
        val result: UvData = OpenMeteoParser.parse(sampleResponse)
        assertEquals(52.52, result.latitude, 0.01)
        assertEquals(13.41, result.longitude, 0.01)
    }

    @Test
    fun `parses timestamp from response`() {
        val result: UvData = OpenMeteoParser.parse(sampleResponse)
        assertNotNull(result.timestamp)
    }
}