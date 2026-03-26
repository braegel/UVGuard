package de.braegel.uvgard.data.repository

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OkHttpUvClientTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpUvClient

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        client = OkHttpUvClient()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `returns response body as string`() = runTest {
        server.enqueue(MockResponse().setBody("""{"uv_index": 5.2}"""))
        val url = server.url("/test").toString()

        val result = client.get(url)

        assertEquals("""{"uv_index": 5.2}""", result)
    }

    @Test
    fun `throws exception on http error`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))
        val url = server.url("/test").toString()

        var thrown = false
        try {
            client.get(url)
        } catch (e: Exception) {
            thrown = true
        }
        assertTrue("Expected exception on HTTP 500", thrown)
    }

    @Test
    fun `sends correct request url`() = runTest {
        server.enqueue(MockResponse().setBody("{}"))
        val url = server.url("/api/v1/uvi?latitude=52.52&longitude=13.41").toString()

        client.get(url)

        val request = server.takeRequest()
        assertEquals("/api/v1/uvi?latitude=52.52&longitude=13.41", request.path)
    }
}
