package in2000.gruppe8.ui.climate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import in2000.gruppe8.R
import in2000.gruppe8.model.plants.ClimateSignal
import in2000.gruppe8.ui.components.BeeLoader

@Composable
fun ClimateScreen(
    viewModel: ClimateStatisticsViewModel,
    navController: NavController,
) {
    val uiState by viewModel.climateStatisticsUiState.collectAsState()
    val hasData = uiState.earlySummerPrecipitation != null
            && uiState.lateSummerPrecipitation != null
            && uiState.yearlyHeavyRain.isNotEmpty()
    var chartType by remember { mutableStateOf(ChartType.Bar) }
    var showChangeLocationDialog by remember { mutableStateOf(false) }
    val scrollstate = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollstate),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showChangeLocationDialog) {
            AlertDialog(
                onDismissRequest = { showChangeLocationDialog = false },
                title = { Text(stringResource(R.string.change_location_dialog_title)) },
                text = { Text(stringResource(R.string.change_location_dialog_text)) },
                confirmButton = {
                    TextButton(onClick = {
                        showChangeLocationDialog = false
                        navController.navigate("map")
                    }) { Text(stringResource(R.string.change_location_dialog_confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = { showChangeLocationDialog = false }) {
                        Text(stringResource(R.string.change_location_dialog_dismiss))
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                textContentColor = MaterialTheme.colorScheme.onBackground,
            )
        }
        // Make the entire row clickable to avoid accidentally tapping the edit-location icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable(
                onClickLabel = stringResource(R.string.change_location),
                onClick = { showChangeLocationDialog = true }
            )
        ) {
            Text(
                text = stringResource(R.string.statistics_for_location, uiState.location ?: ""),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() }
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_edit_location),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                uiState.isLoading && !hasData -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BeeLoader()
                            Text(
                                text = stringResource(R.string.loading_climate_data),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                uiState.error != null && !hasData -> {
                    Text(
                        text = stringResource(R.string.climate_data_error),
                        modifier = Modifier.padding(16.dp)
                    )
                }

                hasData -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ClimateSignalsSection(uiState = uiState)

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.key_figures),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.semantics { heading() }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SummerPrecipitationCard(
                                    uiState = uiState,
                                    modifier = Modifier.weight(1f)
                                )
                                HeavyRainCard(
                                    uiState = uiState,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.statistics),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.semantics { heading() }
                            )

                            ChartTypeSegmentedControl(
                                selected = chartType,
                                onSelectedChange = { chartType = it },
                            )

                            ClimateChartsCarousel(
                                state = uiState,
                                chartType = chartType,
                            )
                        }
                    }
                }

                // Fallback: data exists but neither loading nor error — should not normally occur
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BeeLoader()
                            Text(
                                stringResource(R.string.loading_climate_data),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ClimateSignalChip(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = backgroundColor
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClimateSignalsSection(uiState: ClimateStatisticsUiState) {
    val (summerSignalText, summerBg, summerFg) = when (uiState.summerPrecipitationTrend) {
        ClimateSignal.DRY_SUMMER -> Triple(stringResource(R.string.dryer_summers), MaterialTheme.colorScheme.tertiary.copy(0.5f), MaterialTheme.colorScheme.onTertiary)
        ClimateSignal.WET_SUMMER -> Triple(stringResource(R.string.wetter_summers), MaterialTheme.colorScheme.tertiary.copy(0.5f), MaterialTheme.colorScheme.onTertiary)
        else -> Triple(stringResource(R.string.stable_summers), MaterialTheme.colorScheme.surface.copy(0.5f), MaterialTheme.colorScheme.onSurface)
    }

    val (heavyRainText, heavyRainBg, heavyRainFg) = when (uiState.heavyRainTrend) {
        ClimateSignal.HEAVY_RAIN -> Triple(stringResource(R.string.more_rain), MaterialTheme.colorScheme.tertiary.copy(0.5f), MaterialTheme.colorScheme.onTertiary)
        else -> Triple(stringResource(R.string.no_change_rain), MaterialTheme.colorScheme.surface.copy(0.5f), MaterialTheme.colorScheme.onSurface)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.climate_heading),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() }
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ClimateSignalChip(
                text = summerSignalText,
                backgroundColor = summerBg,
                contentColor = summerFg
            )

            ClimateSignalChip(
                text = heavyRainText,
                backgroundColor = heavyRainBg,
                contentColor = heavyRainFg
            )
        }
    }
}