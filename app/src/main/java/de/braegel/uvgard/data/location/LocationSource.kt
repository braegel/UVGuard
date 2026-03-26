package de.braegel.uvgard.data.location

interface LocationSource {
    suspend fun getLocation(): LocationData
}
