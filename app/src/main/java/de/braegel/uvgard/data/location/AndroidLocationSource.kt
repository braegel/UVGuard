package de.braegel.uvgard.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

class AndroidLocationSource(context: Context) : LocationSource {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(): LocationData {
        val location = fusedClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            null
        ).await()
            ?: fusedClient.lastLocation.await()
            ?: throw IllegalStateException("Unable to determine location")

        return LocationData(
            latitude = location.latitude,
            longitude = location.longitude
        )
    }
}
