package de.braegel.uvgard.data.api

import de.braegel.uvgard.data.model.UvData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
private data class OpenMeteoResponse(
    val latitude: Double,
    val longitude: Double,
    val current: Current,
    val daily: Daily
) {
    @Serializable
    data class Current(
        val time: String,
        @SerialName("uv_index") val uvIndex: Double
    )

    @Serializable
    data class Daily(
        val time: List<String>,
        @SerialName("uv_index_max") val uvIndexMax: List<Double>
    )
}

object OpenMeteoParser {

    fun parse(jsonString: String): UvData {
        val response = uvJson.decodeFromString<OpenMeteoResponse>(jsonString)
        return UvData(
            currentUvIndex = response.current.uvIndex,
            dailyMaxUvIndex = response.daily.uvIndexMax.first(),
            latitude = response.latitude,
            longitude = response.longitude,
            timestamp = response.current.time
        )
    }
}
