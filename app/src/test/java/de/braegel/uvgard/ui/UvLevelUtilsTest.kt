package de.braegel.uvgard.ui

import de.braegel.uvgard.ui.theme.UvExtreme
import de.braegel.uvgard.ui.theme.UvHigh
import de.braegel.uvgard.ui.theme.UvLow
import de.braegel.uvgard.ui.theme.UvModerate
import de.braegel.uvgard.ui.theme.UvVeryHigh
import org.junit.Assert.assertEquals
import org.junit.Test

class UvLevelUtilsTest {

    // --- getUvColor ---

    @Test
    fun colorIsLowForUvBelow3() {
        assertEquals(UvLow, getUvColor(0.0))
        assertEquals(UvLow, getUvColor(2.9))
    }

    @Test
    fun colorIsModerateForUv3to5() {
        assertEquals(UvModerate, getUvColor(3.0))
        assertEquals(UvModerate, getUvColor(5.9))
    }

    @Test
    fun colorIsHighForUv6to7() {
        assertEquals(UvHigh, getUvColor(6.0))
        assertEquals(UvHigh, getUvColor(7.9))
    }

    @Test
    fun colorIsVeryHighForUv8to10() {
        assertEquals(UvVeryHigh, getUvColor(8.0))
        assertEquals(UvVeryHigh, getUvColor(10.9))
    }

    @Test
    fun colorIsExtremeForUv11Plus() {
        assertEquals(UvExtreme, getUvColor(11.0))
        assertEquals(UvExtreme, getUvColor(15.0))
    }

    @Test
    fun colorIsLowForNegativeUv() {
        assertEquals(UvLow, getUvColor(-1.0))
    }

    // --- getUvLevelLabel ---

    @Test
    fun labelIsLowBelow3() {
        assertEquals("Low", getUvLevelLabel(0.0))
        assertEquals("Low", getUvLevelLabel(2.9))
    }

    @Test
    fun labelIsModerateAt3() {
        assertEquals("Moderate", getUvLevelLabel(3.0))
    }

    @Test
    fun labelIsHighAt6() {
        assertEquals("High", getUvLevelLabel(6.0))
    }

    @Test
    fun labelIsVeryHighAt8() {
        assertEquals("Very High", getUvLevelLabel(8.0))
    }

    @Test
    fun labelIsExtremeAt11() {
        assertEquals("Extreme", getUvLevelLabel(11.0))
    }

    // --- getUvAdvice ---

    @Test
    fun adviceDiffersPerLevel() {
        val adviceLow = getUvAdvice(1.0)
        val adviceModerate = getUvAdvice(4.0)
        val adviceHigh = getUvAdvice(7.0)
        val adviceVeryHigh = getUvAdvice(9.0)
        val adviceExtreme = getUvAdvice(12.0)

        val allAdvice = setOf(adviceLow, adviceModerate, adviceHigh, adviceVeryHigh, adviceExtreme)
        assertEquals("Each UV level should have unique advice", 5, allAdvice.size)
    }

    @Test
    fun adviceBoundaryAt3() {
        val below = getUvAdvice(2.9)
        val at = getUvAdvice(3.0)
        assert(below != at) { "Advice should change at boundary 3" }
    }

    @Test
    fun adviceBoundaryAt6() {
        val below = getUvAdvice(5.9)
        val at = getUvAdvice(6.0)
        assert(below != at) { "Advice should change at boundary 6" }
    }

    @Test
    fun adviceBoundaryAt8() {
        val below = getUvAdvice(7.9)
        val at = getUvAdvice(8.0)
        assert(below != at) { "Advice should change at boundary 8" }
    }

    @Test
    fun adviceBoundaryAt11() {
        val below = getUvAdvice(10.9)
        val at = getUvAdvice(11.0)
        assert(below != at) { "Advice should change at boundary 11" }
    }
}
