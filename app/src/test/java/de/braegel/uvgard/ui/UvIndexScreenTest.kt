package de.braegel.uvgard.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import de.braegel.uvgard.data.model.UvData
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UvIndexScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val openMeteoData = UvData(
        currentUvIndex = 5.23,
        dailyMaxUvIndex = 7.81,
        latitude = 52.52,
        longitude = 13.41,
        timestamp = "2025-07-15T12:00",
        source = "Open-Meteo"
    )

    private val currentUvIndexData = UvData(
        currentUvIndex = 5.07,
        dailyMaxUvIndex = 7.55,
        latitude = 52.52,
        longitude = 13.41,
        timestamp = "2025-07-15T12:05:00Z",
        source = "CurrentUVIndex"
    )

    // --- UV values rounded to one decimal ---

    @Test
    fun displaysCurrentUvRoundedToOneDecimal() {
        composeTestRule.setContent {
            UvIndexScreen(uvDataList = listOf(openMeteoData, currentUvIndexData))
        }
        composeTestRule.onNodeWithText("5.2", substring = true).assertIsDisplayed()
    }

    @Test
    fun displaysSecondSourceUvRoundedToOneDecimal() {
        composeTestRule.setContent {
            UvIndexScreen(uvDataList = listOf(openMeteoData, currentUvIndexData))
        }
        composeTestRule.onNodeWithText("5.1", substring = true).assertExists()
    }

    @Test
    fun displaysDailyMaxRoundedToOneDecimal() {
        composeTestRule.setContent {
            UvIndexScreen(uvDataList = listOf(openMeteoData, currentUvIndexData))
        }
        composeTestRule.onNodeWithText("7.8", substring = true).assertIsDisplayed()
    }

    // --- Both sources visible ---

    @Test
    fun displaysOpenMeteoSource() {
        composeTestRule.setContent {
            UvIndexScreen(uvDataList = listOf(openMeteoData, currentUvIndexData))
        }
        composeTestRule.onNodeWithText("Open-Meteo", substring = true).assertIsDisplayed()
    }

    @Test
    fun displaysCurrentUvIndexSource() {
        composeTestRule.setContent {
            UvIndexScreen(uvDataList = listOf(openMeteoData, currentUvIndexData))
        }
        composeTestRule.onNodeWithText("CurrentUVIndex", substring = true).assertExists()
    }

    // --- Side by side layout ---

    @Test
    fun bothSourceCardsInSameRow() {
        composeTestRule.setContent {
            UvIndexScreen(uvDataList = listOf(openMeteoData, currentUvIndexData))
        }
        composeTestRule.onNodeWithTag("uv_cards_row").assertIsDisplayed()
    }

    // --- Slider label ---

    @Test
    fun displaysSliderWithWarningLabel() {
        composeTestRule.setContent {
            UvIndexScreen(
                uvDataList = listOf(openMeteoData, currentUvIndexData),
                threshold = 6.0f
            )
        }
        composeTestRule.onNodeWithText("Warning at level 6", substring = true, ignoreCase = true)
            .performScrollTo().assertIsDisplayed()
    }

    // --- Timestamps with date and time ---

    @Test
    fun displaysTimestampWithDateAndTime() {
        composeTestRule.setContent {
            UvIndexScreen(uvDataList = listOf(openMeteoData, currentUvIndexData))
        }
        composeTestRule.onAllNodesWithText("2025-07-15", substring = true).assertCountEquals(3)
        composeTestRule.onAllNodesWithText("12:00", substring = true).assertCountEquals(2)
    }

    // --- Location at bottom ---

    @Test
    fun displaysLocationAtBottom() {
        composeTestRule.setContent {
            UvIndexScreen(uvDataList = listOf(openMeteoData, currentUvIndexData))
        }
        composeTestRule.onNodeWithText("52.52", substring = true).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("13.41", substring = true).performScrollTo().assertIsDisplayed()
    }

    // --- Loading states ---

    @Test
    fun showsLoadingStateWhenListIsEmpty() {
        composeTestRule.setContent {
            UvIndexScreen(uvDataList = emptyList())
        }
        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun showsLoadingStateWhenNull() {
        composeTestRule.setContent {
            UvIndexScreen(uvDataList = null)
        }
        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
    }
}
