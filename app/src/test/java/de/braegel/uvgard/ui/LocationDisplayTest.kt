package de.braegel.uvgard.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationDisplayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysLatitude() {
        composeTestRule.setContent {
            LocationDisplay(
                latitude = 52.5186,
                longitude = 13.4081,
                timestamp = "2025-07-15T12:34:56"
            )
        }
        composeTestRule.onNodeWithText("52.52", substring = true).assertIsDisplayed()
    }

    @Test
    fun displaysLongitude() {
        composeTestRule.setContent {
            LocationDisplay(
                latitude = 52.5186,
                longitude = 13.4081,
                timestamp = "2025-07-15T12:34:56"
            )
        }
        composeTestRule.onNodeWithText("13.41", substring = true).assertIsDisplayed()
    }

    @Test
    fun displaysDegreeSymbol() {
        composeTestRule.setContent {
            LocationDisplay(
                latitude = 52.5186,
                longitude = 13.4081,
                timestamp = "2025-07-15T12:34:56"
            )
        }
        composeTestRule.onNodeWithText("\u00B0", substring = true).assertIsDisplayed()
    }

    @Test
    fun displaysTimestamp() {
        composeTestRule.setContent {
            LocationDisplay(
                latitude = 52.5186,
                longitude = 13.4081,
                timestamp = "2025-07-15T12:34:56"
            )
        }
        composeTestRule.onNodeWithText("12:34", substring = true).assertIsDisplayed()
    }

    @Test
    fun roundsToTwoDecimalPlaces() {
        composeTestRule.setContent {
            LocationDisplay(
                latitude = 48.8566123,
                longitude = 2.3522456,
                timestamp = "2025-07-15T09:00:00"
            )
        }
        composeTestRule.onNodeWithText("48.86", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("2.35", substring = true).assertIsDisplayed()
    }

    @Test
    fun displaysNegativeCoordinates() {
        composeTestRule.setContent {
            LocationDisplay(
                latitude = -33.8688,
                longitude = -151.2093,
                timestamp = "2025-07-15T09:00:00"
            )
        }
        composeTestRule.onNodeWithText("-33.87", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("-151.21", substring = true).assertIsDisplayed()
    }
}
