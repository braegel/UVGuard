package de.braegel.uvgard.data.repository

import java.io.IOException
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OkHttpUvClient : UvHttpClient {

    private val client = OkHttpClient()

    override suspend fun get(url: String): String = suspendCancellableCoroutine { cont ->
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)

        cont.invokeOnCancellation { call.cancel() }

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (cont.isActive) cont.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!cont.isActive) return
                if (!response.isSuccessful) {
                    cont.resumeWithException(IOException("HTTP ${response.code}"))
                    return
                }
                val body = response.body?.string()
                if (body != null) {
                    cont.resume(body)
                } else {
                    cont.resumeWithException(IOException("Empty response body"))
                }
            }
        })
    }
}
