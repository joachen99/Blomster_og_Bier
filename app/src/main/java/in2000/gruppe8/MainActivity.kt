package in2000.gruppe8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import in2000.gruppe8.data.cache.JsonDataSourceImplementation
import in2000.gruppe8.data.recommendation.ActionsDataSource
import in2000.gruppe8.data.recommendation.BeeDataSource
import in2000.gruppe8.data.climate.FrostDataSource
import in2000.gruppe8.data.climate.FrostDataSourceImplementation
import in2000.gruppe8.data.climate.WeatherRepository
import in2000.gruppe8.data.climate.WeatherRepositoryImplementation
import in2000.gruppe8.domain.ClimateProfileRepository
import in2000.gruppe8.model.cache.ClimateNormalsCache
import in2000.gruppe8.model.cache.HeavyRainCache
import in2000.gruppe8.model.cache.ObservationCache
import in2000.gruppe8.model.cache.SourceCache
import in2000.gruppe8.navigation.AppNavHost
import in2000.gruppe8.ui.actions.ActionsViewModel
import in2000.gruppe8.ui.bee.BeeViewModel
import in2000.gruppe8.ui.climate.ClimateStatisticsViewModel
import in2000.gruppe8.ui.components.BottomNavBar
import in2000.gruppe8.ui.components.HamburgerMenu
import in2000.gruppe8.ui.components.TopBar
import in2000.gruppe8.ui.components.getTitle
import in2000.gruppe8.ui.map.AddressSearchViewModel
import in2000.gruppe8.ui.map.MapViewModel
import in2000.gruppe8.ui.plant.PlantViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.lang.reflect.Type
import in2000.gruppe8.ui.theme.Gruppe_8Theme


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creating cache files
        val observationsCacheFile = File(filesDir, "ObservationsCache.json")
        val sourceCacheFile = File(filesDir, "SourceCache.json")
        val heavyRainCacheFile = File(filesDir, "HeavyRainCache.json")
        val climateNormalsCacheFile = File(filesDir, "ClimateNormalsCache.json")

        // Creating object types for cache
        val observationCacheType: Type =
            object : TypeToken<List<ObservationCache>>() {}.type
        val sourceCacheType: Type =
            object : TypeToken<List<SourceCache>>() {}.type
        val heavyRainCacheType : Type =
            object : TypeToken<List<HeavyRainCache>>() {}.type
        val climateNormalsCacheType : Type =
            object : TypeToken<List<ClimateNormalsCache>>() {}.type

        val gson = Gson()

        // Creating data sources
        val observationsDataSource =
            JsonDataSourceImplementation<ObservationCache>(observationsCacheFile, observationCacheType, gson)
        val sourceDataSource =
            JsonDataSourceImplementation<SourceCache>(sourceCacheFile, sourceCacheType, gson)
        val heavyRainDataSource =
            JsonDataSourceImplementation<HeavyRainCache>(heavyRainCacheFile, heavyRainCacheType, gson)
        val climateNormalsDataSource =
            JsonDataSourceImplementation<ClimateNormalsCache>(climateNormalsCacheFile, climateNormalsCacheType, gson)
        val frostDataSource: FrostDataSource = FrostDataSourceImplementation()
        val actionsDataSource = ActionsDataSource(applicationContext)
        val beeDataSource = BeeDataSource(applicationContext)

        // Creating repositories and
        val weatherRepository: WeatherRepository = WeatherRepositoryImplementation(
            frostDataSource,
            observationsDataSource,
            sourceDataSource,
            heavyRainDataSource,
            climateNormalsDataSource
        )
        val climateProfileRepository = ClimateProfileRepository(weatherRepository)
        val userPreferencesRepository = (application as OurApplication).userPreferencesRepository

        //Creating viewmodels
        val viewModel = MapViewModel(weatherRepository, userPreferencesRepository)
        val climateViewModel = ClimateStatisticsViewModel(weatherRepository)
        val actionsViewModel = ActionsViewModel(
            actionsDataSource = actionsDataSource,
            beeDataSource = beeDataSource
        )
        val beeViewModel = BeeViewModel(
            actionsDataSource = actionsDataSource,
            beeDataSource = beeDataSource
        )
        val addressSearchViewModel = AddressSearchViewModel()
        val plantViewModel = PlantViewModel(climateProfileRepository)

        setContent {
            Gruppe_8Theme(
                darkTheme = isSystemInDarkTheme(), // kan evt. droppes, har default
                dynamicColor = false
            ) {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val configuration = LocalConfiguration.current
            LaunchedEffect(configuration) {
                drawerState.close()
            }

            val currentRoute = navController
                .currentBackStackEntryAsState().value?.destination?.route

            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = false,
                drawerContent = {
                    HamburgerMenu(
                        drawerState = drawerState,
                        navController = navController
                    )
                }
            ) {
                Scaffold(

                    // Top Bar
                    topBar = {
                        val logoRes = when (currentRoute) {
                            "map" -> R.drawable.bee_icon_without_background_smallest
                            "climate_statistics",
                            "recommended_plants",
                            "bees",
                            "actions",
                            "about_us",
                            "settings" -> R.drawable.bee_icon_without_background_smallest
                            else -> null
                        }
                        TopBar(
                            title = getTitle(currentRoute),
                            showBack =
                                currentRoute?.startsWith("action_info/") == true || currentRoute?.startsWith("bee_info/") == true,
                            onBackClick = { navController.navigateUp() },
                            logoRes = logoRes
                        )
                    },

                    // Bottom navBar
                    bottomBar = {
                        BottomNavBar(
                            navController = navController,
                            onMenuClick = {
                                scope.launch { drawerState.open() }
                            }
                        )
                    }
                )   // Main screen with nav host
                    { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            AppNavHost(
                                navController = navController,
                                mapViewModel = viewModel,
                                climateViewModel = climateViewModel,
                                actionsViewModel = actionsViewModel,
                                addressSearchViewModel = addressSearchViewModel,
                                beeViewModel = beeViewModel,
                                observationsDataSource = observationsDataSource,
                                sourceDataSource = sourceDataSource,
                                heavyRainDataSource = heavyRainDataSource,
                                climateNormalsDataSource = climateNormalsDataSource,
                                plantViewModel = plantViewModel,
                            )
                        }
                }
            }
        }
    }
}
}