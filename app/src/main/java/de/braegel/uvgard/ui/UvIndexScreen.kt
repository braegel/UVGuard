package de.braegel.uvgard.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.braegel.uvgard.R
import de.braegel.uvgard.data.model.UvData
import de.braegel.uvgard.ui.theme.UvExtreme
import de.braegel.uvgard.ui.theme.UvHigh
import de.braegel.uvgard.ui.theme.UvLow
import de.braegel.uvgard.ui.theme.UvModerate
import de.braegel.uvgard.ui.theme.UvVeryHigh

private const val GAUGE_MAX_UV = 14.0

@Composable
fun UvIndexScreen(
    uvDataList: List<UvData>?,
    threshold: Float = 6.0f,
    onThresholdChanged: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (uvDataList == null || uvDataList.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("loading"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.loading_uv_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val primaryUv = uvDataList.maxByOrNull { it.currentUvIndex } ?: uvDataList.first()
    val currentUv = primaryUv.currentUvIndex
    val maxUv = primaryUv.dailyMaxUvIndex

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        UvIndexGauge(
            uvIndex = currentUv,
            modifier = Modifier.testTag("uv_gauge")
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = getUvLevelLabel(currentUv),
            style = MaterialTheme.typography.headlineMedium,
            color = getUvColor(currentUv),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = getUvAdvice(currentUv),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("uv_cards_row"),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uvDataList.forEach { uvData ->
                UvSourceCard(
                    uvData = uvData,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.todays_peak),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = getUvLevelLabel(maxUv),
                        style = MaterialTheme.typography.bodyMedium,
                        color = getUvColor(maxUv)
                    )
                }
                Text(
                    text = "%.1f".format(maxUv),
                    style = MaterialTheme.typography.displayMedium,
                    color = getUvColor(maxUv)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        UvThresholdSlider(
            threshold = threshold,
            onThresholdChanged = onThresholdChanged
        )

        Spacer(modifier = Modifier.height(12.dp))

        LocationDisplay(
            latitude = primaryUv.latitude,
            longitude = primaryUv.longitude,
            timestamp = primaryUv.timestamp
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun UvIndexGauge(uvIndex: Double, modifier: Modifier = Modifier) {
    val animatedProgress by animateFloatAsState(
        targetValue = (uvIndex / GAUGE_MAX_UV).toFloat().coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "uv_progress"
    )
    val uvColor = getUvColor(uvIndex)
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            val strokeWidth = 16.dp.toPx()
            val arcSize = size.width - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            drawArc(
                color = trackColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = uvColor,
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "%.1f".format(uvIndex),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = uvColor
            )
            Text(
                text = stringResource(R.string.uv_index_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun UvSourceCard(uvData: UvData, modifier: Modifier = Modifier) {
    val uvColor = getUvColor(uvData.currentUvIndex)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = uvData.source,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "%.1f".format(uvData.currentUvIndex),
                style = MaterialTheme.typography.headlineLarge,
                color = uvColor,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.uv_current_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.uv_peak_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "%.1f".format(uvData.dailyMaxUvIndex),
                    style = MaterialTheme.typography.titleMedium,
                    color = getUvColor(uvData.dailyMaxUvIndex),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = formatTimestamp(uvData.timestamp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

fun getUvColor(uvIndex: Double): Color = when {
    uvIndex < 3 -> UvLow
    uvIndex < 6 -> UvModerate
    uvIndex < 8 -> UvHigh
    uvIndex < 11 -> UvVeryHigh
    else -> UvExtreme
}

fun getUvLevelLabel(uvIndex: Double): String = when {
    uvIndex < 3 -> "Low"
    uvIndex < 6 -> "Moderate"
    uvIndex < 8 -> "High"
    uvIndex < 11 -> "Very High"
    else -> "Extreme"
}

fun getUvAdvice(uvIndex: Double): String = when {
    uvIndex < 3 -> "No protection needed. Enjoy the outdoors!"
    uvIndex < 6 -> "Wear sunglasses. Use SPF 30+ if outside for long."
    uvIndex < 8 -> "Reduce sun exposure between 10am\u20134pm. SPF 30+ recommended."
    uvIndex < 11 -> "Extra protection needed. Avoid midday sun. SPF 50+."
    else -> "Stay indoors if possible. Maximum protection required."
}

fun formatTimestamp(timestamp: String): String {
    val date = timestamp.substringBefore("T")
    val time = timestamp.substringAfter("T").take(5)
    return "$date $time"
}
