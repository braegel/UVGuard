package de.braegel.uvgard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import de.braegel.uvgard.data.model.UvData

@Composable
fun UvIndexScreen(
    uvDataList: List<UvData>?,
    threshold: Float = 6.0f,
    onThresholdChanged: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (uvDataList == null || uvDataList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().testTag("loading"),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val firstData = uvDataList.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(16.dp)
                .height(IntrinsicSize.Min)
                .testTag("uv_cards_row"),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uvDataList.forEach { uvData ->
                UvSourceCard(
                    uvData = uvData,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UvThresholdSlider(
                threshold = threshold,
                onThresholdChanged = onThresholdChanged
            )

            LocationDisplay(
                latitude = firstData.latitude,
                longitude = firstData.longitude,
                timestamp = firstData.timestamp
            )
        }
    }
}

@Composable
private fun UvSourceCard(uvData: UvData, modifier: Modifier = Modifier) {
    val dateTime = formatTimestamp(uvData.timestamp)

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = uvData.source, style = MaterialTheme.typography.titleMedium)

            Text(text = "Current", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "%.1f".format(uvData.currentUvIndex),
                style = MaterialTheme.typography.displayMedium
            )

            Text(text = "Max", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "%.1f".format(uvData.dailyMaxUvIndex),
                style = MaterialTheme.typography.displayMedium
            )

            Text(text = dateTime, style = MaterialTheme.typography.bodySmall)
        }
    }
}

fun formatTimestamp(timestamp: String): String {
    val date = timestamp.substringBefore("T")
    val time = timestamp.substringAfter("T").take(5)
    return "$date $time"
}
