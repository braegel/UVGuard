package de.braegel.uvgard.data.api

import de.braegel.uvgard.data.model.UvData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
private data class CurrentUvIndexResponse(
    val latitude: Double,
    val longitude: Double,
    val now: Now,
    val forecast: List<ForecastEntry>
) {
    @Serializable
    data class Now(
        val time: String,
        val uvi: Double
    )

    @Serializable
    data class ForecastEntry(
        val time: String,
        val uvi: Double
    )
}

object CurrentUvIndexParser {

    fun parse(jsonString: String): UvData {
        val response = uvJson.decodeFromString<CurrentUvIndexResponse>(jsonString)
        val maxForecast = response.forecast.maxOfOrNull { it.uvi } ?: 0.0
        return UvData(
            currentUvIndex = response.now.uvi,
            dailyMaxUvIndex = maxOf(response.now.uvi, maxForecast),
            latitude = response.latitude,
            longitude = response.longitude,
            timestamp = response.now.time
        )
    }
}