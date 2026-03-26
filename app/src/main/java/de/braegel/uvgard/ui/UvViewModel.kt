package de.braegel.uvgard.ui

import de.braegel.uvgard.data.location.LocationPoller
import de.braegel.uvgard.data.model.UvData
import de.braegel.uvgard.data.repository.UvRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UvViewModel(
    private val locationPoller: LocationPoller,
    private val uvRepository: UvRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uvData = MutableStateFlow<List<UvData>?>(null)
    val uvData: StateFlow<List<UvData>?> = _uvData

    fun start() {
        scope.launch {
            locationPoller.locationFlow().collect { location ->
                val results = uvRepository.fetchAll(location.latitude, location.longitude)
                if (results.isNotEmpty()) {
                    _uvData.value = results
                }
            }
        }
    }
}