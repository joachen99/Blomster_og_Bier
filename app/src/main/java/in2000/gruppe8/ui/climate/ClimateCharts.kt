package in2000.gruppe8.ui.climate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import in2000.gruppe8.R
import io.github.dautovicharis.charts.BarChart
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.BarChartDefaults
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.ChartViewStyle
import io.github.dautovicharis.charts.style.LineChartDefaults

enum class ChartType {
    Line,
    Bar
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartTypeSegmentedControl(
    selected: ChartType,
    onSelectedChange: (ChartType) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        ChartType.Line to stringResource(R.string.lines),
        ChartType.Bar  to stringResource(R.string.bars)
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
        ) {
            options.forEachIndexed { index, (type, label) ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ),
                    onClick = { onSelectedChange(type) },
                    selected = type == selected,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor   = MaterialTheme.colorScheme.primary,
                        activeContentColor     = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.background,
                        inactiveContentColor   = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    label = { Text(label) }
                )
            }
        }
    }
}

@Composable
fun ClimateChartsCarousel(
    state: ClimateStatisticsUiState,
    chartType: ChartType,
    modifier: Modifier = Modifier
) {
    val pages: List<Pair<String, @Composable () -> Unit>> = when (chartType) {
        ChartType.Line -> listOf(
            stringResource(R.string.rain) to { ShowSummerPrecipitationLine(state) },
            stringResource(R.string.destination_actions) to { ShowTorrentialLine(state) },
            stringResource(R.string.yearly_precipitation) to { ShowYearlyPrecipitationLine(state) },
        )
        ChartType.Bar -> listOf(
            stringResource(R.string.rain) to { ShowSummerPrecipitationBar(state) },
            stringResource(R.string.destination_actions) to { ShowTorrentialBar(state) },
            stringResource(R.string.yearly_precipitation) to { ShowYearlyPrecipitationBar(state) },
        )
    }

    var currentPage by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { if (currentPage > 0) currentPage-- },
                enabled = currentPage > 0
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.previous),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            Text(
                text = pages[currentPage].first,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(
                onClick = { if (currentPage < pages.lastIndex) currentPage++ },
                enabled = currentPage < pages.lastIndex
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(R.string.next)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            pages.forEachIndexed { index, _ ->
                val pageDescription = stringResource(R.string.page_indicator_description, index + 1, pages.size)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (index == currentPage) 10.dp else 8.dp)
                        .background(
                            color = if (index == currentPage)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .semantics {
                            contentDescription = pageDescription
                        }
                )
            }
        }

        ChartWithTooltip(
            richTooltipText = stringResource(R.string.chart_tooltip_text),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                pages[currentPage].second()
            }
        }
    }
}

// Hides the chart library's built-in title and legend — the carousel label serves as the title
@Composable
private fun noLegendChartViewStyle(
    width: Dp = Dp.Infinity,
    outerPadding: Dp = 20.dp,
    innerPadding: Dp = 15.dp,
    cornerRadius: Dp = 20.dp,
    shadow: Dp = 1.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
): ChartViewStyle {
    val baseModifier = Modifier
        .wrapContentHeight()
        .padding(outerPadding)
        .shadow(elevation = shadow, shape = RoundedCornerShape(cornerRadius))
        .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
    val modifierMain = if (width.value.isInfinite()) baseModifier.wrapContentWidth()
                       else baseModifier.width(width)
    return ChartViewStyle(
        modifierMain = modifierMain,
        styleTitle = TextStyle.Default,
        modifierTopTitle = Modifier.requiredHeight(0.dp),
        modifierLegend = Modifier.requiredHeight(0.dp),
        innerPadding = innerPadding,
        width = width,
        backgroundColor = backgroundColor,
    )
}

@Composable
fun ShowSummerPrecipitationBar(state: ClimateStatisticsUiState) {
    val values = listOf(
        state.earlySummerPrecipitation?.toDouble() ?: 0.0,
        state.lateSummerPrecipitation?.toDouble() ?: 0.0
    )
    val labels = listOf(stringResource(R.string.period_1961_1990), stringResource(R.string.period_1991_2020))

    val dataSet = values.toChartDataSet(
        title = stringResource(R.string.summer_rain_in_mm),
        labels = labels,
        postfix = stringResource(R.string.millimeters)
    )

    val summerBarDescription = stringResource(
        R.string.summer_precipitation_bar_description,
        state.earlySummerPrecipitation ?: 0,
        state.lateSummerPrecipitation ?: 0
    )

    val style = BarChartDefaults.style(
        yAxisLabelCount = 4,
        chartViewStyle = noLegendChartViewStyle(
            shadow = 2.dp,
            backgroundColor = MaterialTheme.colorScheme.background,
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics(mergeDescendants = true) {
                contentDescription = summerBarDescription
            },
        contentAlignment = Alignment.TopCenter
    ) {
        BarChart(dataSet, style)
    }
}

@Composable
fun ShowSummerPrecipitationLine(state: ClimateStatisticsUiState) {
    val values = listOf(
        state.earlySummerPrecipitation?.toDouble() ?: 0.0,
        state.lateSummerPrecipitation?.toDouble() ?: 0.0
    )

    val labels = listOf(
        stringResource(R.string.period_1961_1990),
        stringResource(R.string.period_1991_2020)
    )

    val dataSet = values.toChartDataSet(
        title = stringResource(R.string.summer_rain_in_mm),
        labels = labels,
        postfix = stringResource(R.string.millimeters)
    )

    val summerLineDescription = stringResource(
        R.string.summer_precipitation_line_description,
        state.earlySummerPrecipitation ?: 0,
        state.lateSummerPrecipitation ?: 0
    )

    val style = LineChartDefaults.style(
        yAxisLabelCount = 5,
        chartViewStyle = noLegendChartViewStyle(
            shadow = 5.dp,
            backgroundColor = MaterialTheme.colorScheme.background,
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics(mergeDescendants = true) {
                contentDescription = summerLineDescription
            },
        contentAlignment = Alignment.TopCenter
    ) {
        LineChart(dataSet = dataSet, style = style)
    }
}

@Composable
fun ShowTorrentialBar(state: ClimateStatisticsUiState) {
    val values = state.yearlyHeavyRain.map { it.second }
    val labels = state.yearlyHeavyRain.map { it.first.toString() }

    val dataSet = values.toChartDataSet(
        title = stringResource(R.string.torrential_rain_in_hours),
        labels = labels,
        postfix = stringResource(R.string.hours)
    )

    val hourText = stringResource(R.string.hours)
    val yearValuesText = state.yearlyHeavyRain.joinToString(", ") { (year, hours) ->
        "$year: $hours $hourText"
    }

    val torrentialBarDescription = stringResource(
        R.string.torrential_bar_description,
        yearValuesText
    )

    val style = BarChartDefaults.style(
        yAxisLabelCount = 4,
        chartViewStyle = noLegendChartViewStyle(
            width = 340.dp,
            cornerRadius = 18.dp,
            shadow = 2.dp,
            backgroundColor = MaterialTheme.colorScheme.background,
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics(mergeDescendants = true) {
                contentDescription = torrentialBarDescription
            },
        contentAlignment = Alignment.TopCenter
    ) {
        BarChart(dataSet, style)
    }
}

@Composable
fun ShowTorrentialLine(state: ClimateStatisticsUiState) {
    val items = listOf(
        stringResource(R.string.destination_actions) to state.yearlyHeavyRain.map { it.second.toFloat() },
        stringResource(R.string.trend) to state.yearlyHeavyRainRegression.map { it.second.toFloat() }
    )

    val labels = state.yearlyHeavyRain.map { it.first.toString() }
    val dataSet = items.toMultiChartDataSet(
        title = stringResource(R.string.torrential_rain_in_hours),
        categories = labels,
        postfix = stringResource(R.string.hours)
    )

    val hourText = stringResource(R.string.hours)
    val yearValuesText = state.yearlyHeavyRain.joinToString(", ") { (year, hours) ->
        "$year: $hours $hourText"
    }

    val torrentialLineDescription = stringResource(
        R.string.torrential_line_description,
        yearValuesText
    )

    val style = LineChartDefaults.style(
        yAxisLabelCount = 5,
        chartViewStyle = noLegendChartViewStyle(
            shadow = 5.dp,
            backgroundColor = MaterialTheme.colorScheme.background,
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics(mergeDescendants = true) {
                contentDescription = torrentialLineDescription
            },
        contentAlignment = Alignment.TopCenter
    ) {
        LineChart(dataSet, style)
    }
}

@Composable
fun ShowYearlyPrecipitationLine(state: ClimateStatisticsUiState) {
    val items = listOf(
        stringResource(R.string.rain) to state.yearlyPrecipitation.map { it.second.toFloat() },
        stringResource(R.string.trend) to state.yearlyPrecipitationRegression.map { it.second.toFloat() }
    )

    val labels = state.yearlyPrecipitation.map { it.first.toString() }

    val dataSet = items.toMultiChartDataSet(
        title = stringResource(
            R.string.yearly_precipitation_range,
            labels.first(),
            labels.last()
        ),
        postfix = stringResource(R.string.millimeters),
        categories = labels
    )

    val style = LineChartDefaults.style(
        yAxisLabelCount = 5,
        chartViewStyle = noLegendChartViewStyle(
            shadow = 5.dp,
            backgroundColor = MaterialTheme.colorScheme.background,
        ),
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LineChart(dataSet, style)
    }
}

@Composable
fun ShowYearlyPrecipitationBar(state: ClimateStatisticsUiState) {
    val values = state.yearlyPrecipitation.map { it.second }
    val labels = state.yearlyPrecipitation.map { it.first.toString() }

    val dataSet = values.toChartDataSet(
        title = stringResource(R.string.yearly_precipitation),
        labels = labels,
        postfix = stringResource(R.string.millimeters)
    )

    val style = BarChartDefaults.style(
        yAxisLabelCount = 4,
        chartViewStyle = ChartViewDefaults.style(
            width = 340.dp,
            cornerRadius = 18.dp,
            shadow = 2.dp,
            backgroundColor = MaterialTheme.colorScheme.background,
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        BarChart(dataSet, style)
    }
}

@Composable
fun ChartWithTooltip(
    richTooltipText: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
    ) {
        // Inner Box fills the parent so the chart renders behind the overlay icons
        Box(modifier = Modifier.matchParentSize()) {
            content()
        }

        IconButton(
            onClick = { showTooltip = !showTooltip },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = stringResource(R.string.graph_info),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedVisibility(
            visible = showTooltip,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 6.dp,
                modifier = Modifier.padding(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .widthIn(max = 260.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.graph_info),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .weight(1f)
                                .semantics { heading() }
                        )
                        IconButton(
                            onClick = { showTooltip = false }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.close_box),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = richTooltipText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
