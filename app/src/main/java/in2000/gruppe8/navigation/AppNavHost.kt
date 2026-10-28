package in2000.gruppe8.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import in2000.gruppe8.R
import in2000.gruppe8.data.cache.JsonDataSource
import in2000.gruppe8.model.cache.ClimateNormalsCache
import in2000.gruppe8.model.cache.HeavyRainCache
import in2000.gruppe8.model.cache.ObservationCache
import in2000.gruppe8.model.cache.SourceCache
import in2000.gruppe8.ui.actions.ActionsInfoScreen
import in2000.gruppe8.ui.actions.ActionsScreen
import in2000.gruppe8.ui.actions.ActionsViewModel
import in2000.gruppe8.ui.bee.BeeInfoScreen
import in2000.gruppe8.ui.bee.BeeScreen
import in2000.gruppe8.ui.bee.BeeViewModel
import in2000.gruppe8.ui.climate.ClimateScreen
import in2000.gruppe8.ui.climate.ClimateStatisticsViewModel
import in2000.gruppe8.ui.home.AboutAppScreen
import in2000.gruppe8.ui.home.SettingsScreen
import in2000.gruppe8.ui.map.AddressSearchViewModel
import in2000.gruppe8.ui.map.MapScreen
import in2000.gruppe8.ui.map.MapViewModel
import in2000.gruppe8.ui.plant.PlantScreen
import in2000.gruppe8.ui.plant.PlantViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    mapViewModel: MapViewModel,
    climateViewModel: ClimateStatisticsViewModel,
    actionsViewModel: ActionsViewModel,
    addressSearchViewModel: AddressSearchViewModel,
    beeViewModel: BeeViewModel,
    observationsDataSource: JsonDataSource<ObservationCache>,
    sourceDataSource: JsonDataSource<SourceCache>,
    heavyRainDataSource: JsonDataSource<HeavyRainCache>,
    climateNormalsDataSource: JsonDataSource<ClimateNormalsCache>,
    plantViewModel: PlantViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "map"
    ) {
        // Recommended plants
        composable(route = "recommended_plants") {
            val state by mapViewModel.mapState.collectAsState()
            val plantState by plantViewModel.uiState.collectAsState()
            val longitude = state.selectedLongitude
            val latitude = state.selectedLatitude

            // Auto-fetches plant data if long and lat is restored from DataStore, but plantViewModel has not fetched data
            LaunchedEffect(longitude, latitude) {
                if (longitude != null && latitude != null && plantState.plants.isEmpty() && !plantState.isLoading) {
                    plantViewModel.loadPlants(longitude, latitude)
                    plantViewModel.setLocationName(state.location)
                }
            }

            // Creates PlantScreen if coordinates is selected
            if (longitude != null && latitude != null) {
                PlantScreen(plantViewModel = plantViewModel,
                    navController = navController)
            } else {
                // Box for error on location selection
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    NoLocationBox(navController)
                }
            }
        }

        // Climate statistics
        composable("climate_statistics") {
            val state by mapViewModel.mapState.collectAsState()
            val climateState by climateViewModel.climateStatisticsUiState.collectAsState()
            val longitude = state.selectedLongitude
            val latitude = state.selectedLatitude

            // Auto-fetches climate data if long and lat is restored from DataStore, but climateViewModel has not fetched data
            LaunchedEffect(longitude, latitude) {
                // does not check for isLoading because default value is set to true (for bee loading icon to appear)
                if (longitude != null && latitude != null && climateState.earlySummerPrecipitation == null) {
                    climateViewModel.loadClimateStatistics(longitude, latitude)
                    climateViewModel.setLocationName(state.location)
                }
            }

            if (longitude != null && latitude != null) {
                ClimateScreen(viewModel = climateViewModel, navController = navController)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    NoLocationBox(navController) // Se nederst i filen
                }
            }
        }

        // Actions against heavy rain
        composable("actions") {
            ActionsScreen(
                navController,
                actionsViewModel
            )
        }

        // Details on actions against heavy rain
        composable("action_info/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ActionsInfoScreen(actionsViewModel = actionsViewModel, actionId = id)
        }

        // Actions for bees
        composable("bees") {
            BeeScreen(
                navController,
                beeViewModel
            )
        }

        // Details on actions for bier
        composable("bee_info/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            BeeInfoScreen(beeViewModel = beeViewModel, beeId = id)
        }

        // Map
        composable("map") {
            MapScreen(
                mapViewModel = mapViewModel,
                climateStatisticsViewModel = climateViewModel,
                plantViewModel = plantViewModel,
                addressSearchViewModel = addressSearchViewModel,
                navController = navController
            )
        }

        // About us
        composable(route = "about_us"){
            AboutAppScreen()
        }

        // Settings
        composable("settings"){
            SettingsScreen(observationsDataSource,
                sourceDataSource,
                heavyRainDataSource,
                climateNormalsDataSource)
        }
    }
}



// Box for when locations is not selecfted yet
@Composable
fun NoLocationBox(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.no_location_message),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = { navController.navigate("map") }
            ) {
                Text(stringResource(R.string.no_location_go_to_map))
            }
        }
    }
}
