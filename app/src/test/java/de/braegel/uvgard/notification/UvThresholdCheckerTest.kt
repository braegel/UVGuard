package de.braegel.uvgard.notification

import de.braegel.uvgard.data.model.UvData
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UvThresholdCheckerTest {

    private fun uvData(currentUvIndex: Double) = UvData(
        currentUvIndex = currentUvIndex,
        dailyMaxUvIndex = currentUvIndex,
        latitude = 52.52,
        longitude = 13.41,
        timestamp = "2025-07-15T12:00",
        source = "Open-Meteo"
    )

    @Test
    fun `returns EXCEEDED when uv index crosses above threshold`() {
        val checker = UvThresholdChecker()
        val result = checker.check(listOf(uvData(6.0)), threshold = 5.0f)
        assertTrue(result is ThresholdEvent.Exceeded)
    }

    @Test
    fun `returns SAFE when uv index drops below threshold after being exceeded`() {
        val checker = UvThresholdChecker()
        checker.check(listOf(uvData(6.0)), threshold = 5.0f) // exceeded
        val result = checker.check(listOf(uvData(3.0)), threshold = 5.0f)
        assertTrue(result is ThresholdEvent.Safe)
    }

    @Test
    fun `returns EXCEEDED when any source exceeds threshold`() {
        val checker = UvThresholdChecker()
        val result = checker.check(
            listOf(uvData(3.0), uvData(6.0)),
            threshold = 5.0f
        )
        assertTrue(result is ThresholdEvent.Exceeded)
    }

    @Test
    fun `does not re-trigger if already exceeded`() {
        val checker = UvThresholdChecker()
        val first = checker.check(listOf(uvData(6.0)), threshold = 5.0f)
        val second = checker.check(listOf(uvData(7.0)), threshold = 5.0f)
        assertTrue(first is ThresholdEvent.Exceeded)
        assertTrue(second is ThresholdEvent.NoChange)
    }

    @Test
    fun `triggers again after dropping below and exceeding again`() {
        val checker = UvThresholdChecker()
        checker.check(listOf(uvData(6.0)), threshold = 5.0f) // exceeded
        checker.check(listOf(uvData(3.0)), threshold = 5.0f) // safe
        val third = checker.check(listOf(uvData(6.0)), threshold = 5.0f)
        assertTrue(third is ThresholdEvent.Exceeded)
    }

    @Test
    fun `returns SAFE only once when dropping below threshold`() {
        val checker = UvThresholdChecker()
        checker.check(listOf(uvData(6.0)), threshold = 5.0f) // exceeded
        val first = checker.check(listOf(uvData(3.0)), threshold = 5.0f)
        val second = checker.check(listOf(uvData(2.0)), threshold = 5.0f)
        assertTrue(first is ThresholdEvent.Safe)
        assertTrue(second is ThresholdEvent.NoChange)
    }

    @Test
    fun `initial state below threshold is NoChange`() {
        val checker = UvThresholdChecker()
        val result = checker.check(listOf(uvData(2.0)), threshold = 5.0f)
        assertTrue(result is ThresholdEvent.NoChange)
    }
}
