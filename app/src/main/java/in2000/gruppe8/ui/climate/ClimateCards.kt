package in2000.gruppe8.ui.climate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import in2000.gruppe8.R
import in2000.gruppe8.model.plants.ClimateSignal
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription


@Composable
private fun ClimateKeyFigureCard(
    title: String,
    value: String,
    unit: String,
    periodLabel: String,
    trendLabel: String,
    trendColor: Color,
    modifier: Modifier = Modifier
) {
    // one combined content description for the whole card
    val combinedDescription = buildString {
        append(title)
        append(", ")
        append(value)
        append(" ")
        append(unit)
        if (periodLabel.isNotBlank()) {
            append(". ")
            append(periodLabel)
        }
        if (trendLabel.isNotBlank()) {
            append(". ")
            append(trendLabel)
        }
        append(".")
    }
    Surface(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = combinedDescription},
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface.copy(0.5f)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = periodLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = trendLabel,
                style = MaterialTheme.typography.bodySmall,
                color = trendColor
            )
        }
    }
}

@Composable
fun SummerPrecipitationCard(
    uiState: ClimateStatisticsUiState,
    modifier: Modifier = Modifier
) {
    val totalSummerPrecip = uiState.lateSummerPrecipitation

    val (trendText, trendColor) = when (uiState.summerPrecipitationTrend) {
        ClimateSignal.DRY_SUMMER -> stringResource(R.string.descending) to MaterialTheme.colorScheme.onSurface
        ClimateSignal.WET_SUMMER -> stringResource(R.string.rising) to MaterialTheme.colorScheme.onSurface
        else -> stringResource(R.string.stable) to MaterialTheme.colorScheme.onSurface
    }

    ClimateKeyFigureCard(
        title = stringResource(R.string.summer_rain),
        value = totalSummerPrecip.toString(),
        unit = stringResource(R.string.millimeters),
        periodLabel = stringResource(R.string.summer_precip_period),
        trendLabel = trendText,
        trendColor = trendColor,
        modifier = modifier
    )
}

@Composable
fun HeavyRainCard(
    uiState: ClimateStatisticsUiState,
    modifier: Modifier = Modifier
) {
    val lastEntry = uiState.yearlyHeavyRain.lastOrNull()
    val currentHeavyRainHours = lastEntry?.second ?: 0
    val lastYear = lastEntry?.first

    val (trendText, trendColor) = when (uiState.heavyRainTrend) {
        ClimateSignal.HEAVY_RAIN -> stringResource(R.string.increasing) to MaterialTheme.colorScheme.onTertiary
        else -> stringResource(R.string.stable) to MaterialTheme.colorScheme.onTertiary
    }

    val periodLabel = if (lastYear != null)
        stringResource(R.string.heavy_rain_year, lastYear)
    else
        ""

    ClimateKeyFigureCard(
        title = stringResource(R.string.torrential_rain_hours),
        value = currentHeavyRainHours.toString(),
        unit = stringResource(R.string.hours),
        periodLabel = periodLabel,
        trendLabel = trendText,
        trendColor = trendColor,
        modifier = modifier
    )
}
