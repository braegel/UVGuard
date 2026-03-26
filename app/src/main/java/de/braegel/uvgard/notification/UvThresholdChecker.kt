package de.braegel.uvgard.notification

import de.braegel.uvgard.data.model.UvData

sealed class ThresholdEvent {
    object Exceeded : ThresholdEvent()
    object Safe : ThresholdEvent()
    object NoChange : ThresholdEvent()
}

class UvThresholdChecker {

    private var wasExceeded: Boolean? = null

    fun check(uvDataList: List<UvData>, threshold: Float): ThresholdEvent {
        val isExceeded = uvDataList.any { it.currentUvIndex > threshold }

        val event = when {
            isExceeded && wasExceeded != true -> ThresholdEvent.Exceeded
            !isExceeded && wasExceeded == true -> ThresholdEvent.Safe
            else -> ThresholdEvent.NoChange
        }

        wasExceeded = isExceeded
        return event
    }
}
