package de.braegel.uvgard.data.repository

import de.braegel.uvgard.data.api.CurrentUvIndexParser
import de.braegel.uvgard.data.api.OpenMeteoParser
import de.braegel.uvgard.data.model.UvData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class UvRepository(private val httpClient: UvHttpClient) {

    suspend fun fetchFromOpenMeteo(latitude: Double, longitude: Double): UvData {
        val url = "$OPEN_METEO_BASE?latitude=$latitude&longitude=$longitude&current=uv_index&daily=uv_index_max&timezone=auto"
        val response = httpClient.get(url)
        return OpenMeteoParser.parse(response).copy(source = SOURCE_OPEN_METEO)
    }

    suspend fun fetchFromCurrentUvIndex(latitude: Double, longitude: Double): UvData {
        val url = "$CURRENT_UV_INDEX_BASE?latitude=$latitude&longitude=$longitude"
        val response = httpClient.get(url)
        return CurrentUvIndexParser.parse(response).copy(source = SOURCE_CURRENT_UV_INDEX)
    }

    suspend fun fetch(latitude: Double, longitude: Double): UvData? {
        return fetchAll(latitude, longitude).firstOrNull()
    }

    suspend fun fetchAll(latitude: Double, longitude: Double): List<UvData> = coroutineScope {
        val openMeteo = async {
            try { fetchFromOpenMeteo(latitude, longitude) } catch (_: Exception) { null }
        }
        val currentUv = async {
            try { fetchFromCurrentUvIndex(latitude, longitude) } catch (_: Exception) { null }
        }
        listOfNotNull(openMeteo.await(), currentUv.await())
    }

    companion object {
        const val OPEN_METEO_BASE = "https://api.open-meteo.com/v1/forecast"
        const val CURRENT_UV_INDEX_BASE = "https://currentuvindex.com/api/v1/uvi"
        const val SOURCE_OPEN_METEO = "Open-Meteo"
        const val SOURCE_CURRENT_UV_INDEX = "CurrentUVIndex"
    }
}
