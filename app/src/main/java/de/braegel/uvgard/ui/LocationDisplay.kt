package de.braegel.uvgard.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LocationDisplay(
    latitude: Double,
    longitude: Double,
    timestamp: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${"%.2f".format(latitude)}° / ${"%.2f".format(longitude)}°",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = formatTimestamp(timestamp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
