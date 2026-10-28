package in2000.gruppe8.ui.plant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import in2000.gruppe8.R
import in2000.gruppe8.data.plants.PlantEntry
import in2000.gruppe8.model.plants.ClimateSignal
import in2000.gruppe8.model.plants.WateringNeed
import in2000.gruppe8.ui.climate.ClimateSignalChip

@Composable
fun PlantScreen(
    plantViewModel: PlantViewModel,
    navController: NavController,
) {
    val uiState by plantViewModel.uiState.collectAsState()
    var showChangeLocationDialog by remember { mutableStateOf(false) }

    if (showChangeLocationDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.change_location_dialog_title)) },
            text = { Text(stringResource(R.string.change_location_dialog_text)) },
            confirmButton = {
                TextButton(onClick = {
                    navController.navigate("map")
                }) { Text(stringResource(R.string.change_location_dialog_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { }) {
                    Text(stringResource(R.string.change_location_dialog_dismiss))
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            textContentColor = MaterialTheme.colorScheme.onBackground,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {        // Makes the entire row clickable, so the user does not have to press the change-location icon
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable {
            }
        ) {
            Text(
                text = stringResource(R.string.plant_screen_title, uiState.location ?: ""),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            // icon to change location:
            Icon(
                painter = painterResource(id = R.drawable.ic_edit_location),
                contentDescription = stringResource(R.string.plant_edit_location),
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary

            )
        }

        val drySummerLabel = stringResource(R.string.dryer_summers)
        val wetSummerLabel = stringResource(R.string.wetter_summers)
        val heavyRainLabel = stringResource(R.string.more_rain)
        val stableLabel = stringResource(R.string.stable_summers)

        uiState.climateSignal?.let { signal ->
            val (chipText, chipBg, chipFg) = when (signal) {// The color does not change with the theme, hardcoded somewhere else as well — technical debt
                ClimateSignal.DRY_SUMMER -> Triple(drySummerLabel, MaterialTheme.colorScheme.tertiary.copy(0.5f), MaterialTheme.colorScheme.onTertiary)
                ClimateSignal.WET_SUMMER -> Triple(wetSummerLabel, MaterialTheme.colorScheme.tertiary.copy(0.5f), MaterialTheme.colorScheme.onTertiary)
                ClimateSignal.HEAVY_RAIN -> Triple(heavyRainLabel, MaterialTheme.colorScheme.tertiary.copy(0.5f), MaterialTheme.colorScheme.onTertiary)
                ClimateSignal.STABLE    -> Triple(stableLabel, MaterialTheme.colorScheme.surface.copy(0.5f), MaterialTheme.colorScheme.onTertiary)

            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.plant_climate_basis),
                    style = MaterialTheme.typography.titleMedium,
                    lineHeight = 28.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                ClimateSignalChip(
                    text = chipText,
                    backgroundColor = chipBg,
                    contentColor = chipFg
                )
            }
        }

        when {
            uiState.isLoading -> Text(stringResource(R.string.plant_loading))
            uiState.errorMessage != null -> Text(text = uiState.errorMessage ?: "")
            uiState.plants.isEmpty() -> Text(stringResource(R.string.plant_no_results))
            else -> {
                // Lists recommended plants in a LazyColumn (should we switch to LazyVerticalGrid?)
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.plants) { plant ->
                        PlantEntryCard(plant = plant)
                    }
                }
            }
        }
    }
}


@Composable
private fun PlantEntryCard(plant: PlantEntry) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isFavorite by rememberSaveable { mutableStateOf(false) } // Both should now survive rotation

    val wateringDisplayName = when (plant.watering) {
        WateringNeed.LOW -> stringResource(R.string.plant_watering_low)
        WateringNeed.MEDIUM -> stringResource(R.string.plant_watering_medium)
        WateringNeed.HIGH -> stringResource(R.string.plant_watering_high)
    }
    val monthNames = listOf(
        stringResource(R.string.month_january),
        stringResource(R.string.month_february),
        stringResource(R.string.month_march),
        stringResource(R.string.month_april),
        stringResource(R.string.month_may),
        stringResource(R.string.month_june),
        stringResource(R.string.month_july),
        stringResource(R.string.month_august),
        stringResource(R.string.month_september),
        stringResource(R.string.month_october),
        stringResource(R.string.month_november),
        stringResource(R.string.month_december)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        //    .clickable { expanded = !expanded }, Removed so it is possible to use a screen reader
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            plant.imageRes?.let { resId ->
                AsyncImage(
                    model = resId,
                    contentDescription = stringResource(plant.nameRes),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(plant.nameRes),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(15.dp))

                    // Favorite button
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier
                            // Touch target must be at least 48.dp
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite)
                                Icons.Filled.Favorite        // Marked as favorite
                            else
                                Icons.Outlined.FavoriteBorder, // Not marked as favorite
                            contentDescription = if (isFavorite)
                                stringResource(R.string.favorite_remove)
                            else
                                stringResource(R.string.favorite_add),
                            tint = if (isFavorite)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    // Added click handling here so it makes sense for a screen reader
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(48.dp) // To guarantee a large enough touch target
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) {
                                stringResource(R.string.expand_close) // Closes
                            } else {
                                stringResource(
                                    R.string.expand_open,
                                    stringResource(plant.nameRes)
                                )  // Opens
                            },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Column(modifier = Modifier
                    // Merges all text on the front of the card for screen reader users
                    .semantics(mergeDescendants = true) {}) {
                    Text(
                        text = stringResource(plant.latinNameRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.plant_hardiness_zone, plant.hardinessZone),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            // Merges all text on the front of the card for screen reader users
                            .semantics(mergeDescendants = true) {}
                        ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(plant.descriptionRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.plant_tips, stringResource(plant.tipsRes)),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.plant_bloom_months, plant.bloomMonths.joinToString(", ") { monthNames[it - 1] }),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.plant_watering, wateringDisplayName),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (plant.goodForBees) stringResource(R.string.plant_bee_friendly_yes) else stringResource(R.string.plant_bee_friendly_no),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}