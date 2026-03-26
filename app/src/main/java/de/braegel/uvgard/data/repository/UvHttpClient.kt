package de.braegel.uvgard.data.repository

interface UvHttpClient {
    suspend fun get(url: String): String
}
