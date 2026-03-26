package de.braegel.uvgard.data.location

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationPoller(
    private val locationSource: LocationSource,
    private val intervalMs: Long
) {
    fun locationFlow(): Flow<LocationData> = flow {
        while (true) {
            try {
                emit(locationSource.getLocation())
            } catch (e: Exception) {
                if (e is CancellationException) throw e
            }
            delay(intervalMs)
        }
    }
}