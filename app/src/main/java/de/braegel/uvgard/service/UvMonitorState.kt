package de.braegel.uvgard.service

import de.braegel.uvgard.data.model.UvData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UvMonitorState {

    private val _uvData = MutableStateFlow<List<UvData>?>(null)
    val uvData: StateFlow<List<UvData>?> = _uvData

    private val _threshold = MutableStateFlow(6.0f)
    val threshold: StateFlow<Float> = _threshold

    fun updateUvData(data: List<UvData>) {
        _uvData.value = data
    }

    fun setThreshold(value: Float) {
        _threshold.value = value
    }
}
