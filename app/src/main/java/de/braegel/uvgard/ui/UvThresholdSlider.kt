package de.braegel.uvgard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.braegel.uvgard.R
import kotlin.math.roundToInt

@Composable
fun UvThresholdSlider(
    threshold: Float,
    onThresholdChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val thresholdColor = getUvColor(threshold.toDouble())

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.alert_threshold) + " ",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${threshold.roundToInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = thresholdColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = getUvLevelLabel(threshold.toDouble()),
                style = MaterialTheme.typography.bodyMedium,
                color = thresholdColor
            )

            Slider(
                value = threshold,
                onValueChange = onThresholdChanged,
                valueRange = 1f..11f,
                steps = 9,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .testTag("threshold_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = thresholdColor,
                    activeTrackColor = thresholdColor,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}