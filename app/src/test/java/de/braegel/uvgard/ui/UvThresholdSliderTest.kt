package de.braegel.uvgard.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UvThresholdSliderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysSlider() {
        composeTestRule.setContent {
            UvThresholdSlider(threshold = 5.0f, onThresholdChanged = {})
        }
        composeTestRule.onNodeWithTag("threshold_slider").assertIsDisplayed()
    }

    @Test
    fun displaysCurrentThresholdValue() {
        composeTestRule.setContent {
            UvThresholdSlider(threshold = 5.0f, onThresholdChanged = {})
        }
        composeTestRule.onNodeWithText("5", substring = true).assertIsDisplayed()
    }

    @Test
    fun displaysThresholdLabel() {
        composeTestRule.setContent {
            UvThresholdSlider(threshold = 5.0f, onThresholdChanged = {})
        }
        composeTestRule.onNodeWithText("Alert Threshold", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun callsOnThresholdChangedWhenSliderMoved() {
        var changed = false
        composeTestRule.setContent {
            UvThresholdSlider(threshold = 3.0f, onThresholdChanged = { changed = true })
        }
        composeTestRule.onNodeWithTag("threshold_slider")
            .performTouchInput {
                swipeRight(startX = centerX - 50f, endX = centerX + 50f)
            }
        assertTrue("onThresholdChanged should be called", changed)
    }
}
