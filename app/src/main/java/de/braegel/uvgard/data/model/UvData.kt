package de.braegel.uvgard.data.model

data class UvData(
    val currentUvIndex: Double,
    val dailyMaxUvIndex: Double,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val source: String = ""
)