package de.braegel.uvgard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun UvThresholdSlider(
    threshold: Float,
    onThresholdChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Warning at level ${threshold.roundToInt()}",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            value = threshold,
            onValueChange = onThresholdChanged,
            valueRange = 1f..11f,
            steps = 9,
            modifier = Modifier.testTag("threshold_slider")
        )
    }
}
