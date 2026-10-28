package in2000.gruppe8.ui.map


import android.R.attr.resource
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import in2000.gruppe8.R
import in2000.gruppe8.ui.climate.ClimateStatisticsViewModel
import in2000.gruppe8.ui.plant.PlantViewModel


@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    climateStatisticsViewModel: ClimateStatisticsViewModel,
    plantViewModel: PlantViewModel,
    addressSearchViewModel: AddressSearchViewModel,
    navController: NavController
) {
    val uiState by mapViewModel.mapState.collectAsState()
    val searchQuery by addressSearchViewModel.query.collectAsState()
    val searchResults by addressSearchViewModel.results.collectAsState()
    val isSearchLoading by addressSearchViewModel.isLoading.collectAsState()
    val resources = LocalResources.current

    // Bee pin
    val bitmap = remember (resource) {
        BitmapFactory.decodeResource(
            resources,
        R.drawable.pin_bee
        )
    }

    // Gets the pin position from the ViewModel so it is remembered across navigation
    val clickedCoordinates: Point? = remember(uiState.pinLongitude, uiState.pinLatitude) {
        val long = uiState.pinLongitude
        val lat = uiState.pinLatitude
        if (long != null && lat != null) { Point.fromLngLat(long, lat) }
            else {null}
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            // If the pin has already been placed (user comes back to the screen), move the camera to the pin
            val savedLong = uiState.pinLongitude
            val savedLat = uiState.pinLatitude
            if (savedLong != null && savedLat != null) {
                zoom(7.0)
                center(Point.fromLngLat(savedLong, savedLat))
            } else {
                // Move to default position
                zoom(7.0)
                center(Point.fromLngLat(10.746083, 59.912722))
            }
            pitch(0.0)
            bearing(0.0)
        }
    }

    var flyToOnNextChange by remember { mutableStateOf(false) }

    // Measures the height of BottomCard so the zoom buttons can be lifted above it
    var bottomCardHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    // Resets the measured height when the card is not shown, so the zoom buttons fall back to the bottom
    LaunchedEffect(uiState.screenState) {
        if (uiState.screenState == MapScreenState.NoSelection) {
            bottomCardHeight = 0.dp
        }
    }

    LaunchedEffect(clickedCoordinates) {
        clickedCoordinates?.let { point ->
            // Get the place name as soon as the pin is set
                mapViewModel.getName(point.longitude(), point.latitude())

            // Used by address search, moves the camera to the selected location
            if (flyToOnNextChange) {
                mapViewportState.flyTo(
                    CameraOptions.Builder().center(point).zoom(13.0).build()
                )
                flyToOnNextChange = false
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // The map itself
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            scaleBar = {
                ScaleBar(Modifier.padding(top = 60.dp))
            },
            logo = {
                Logo(Modifier.padding(bottom = 40.dp)
                    // TalkBack skips logo
                    .clearAndSetSemantics {  }
                    )
            },
            attribution = {
                Attribution(Modifier.padding(bottom = 40.dp)
                    // TalkBack skips logo
                    .clearAndSetSemantics {  }
                )
            },
            onMapClickListener = { point ->
                mapViewModel.placePin(
                    longitude = point.longitude(),
                    latitude = point.latitude())
                true
            }
        ) {
            clickedCoordinates?.let { point ->
                PointAnnotationGroup(
                    annotations = listOf(
                        PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(bitmap)
                            .withIconSize(0.05)
                    )
                )
            }
        }

        // Instruction box at the top
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
            //.padding(16.dp)
        ) {
            // Instruction box is hidden completely when data has been loaded
            if (uiState.screenState != MapScreenState.DataLoaded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // .clip(RoundedCornerShape(12.dp))
                        .background(colorScheme.tertiary)
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(5.dp))
                    // Changes the top-row info box depending on the UI state
                    when (uiState.screenState) {
                        // When the pin is not placed
                        MapScreenState.NoSelection -> {
                            Icon(painter = painterResource(id = R.drawable.ic_info_empty),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                modifier = Modifier.semantics {heading() },
                                text = stringResource(R.string.map_instruction_no_selection),
                                style = MaterialTheme.typography.titleLarge,
                                color = colorScheme.onSurface
                            )
                        }
                        // When the pin is placed
                        MapScreenState.PinPlaced -> {
                            Icon(painter = painterResource(id = R.drawable.ic_info_empty),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                modifier = Modifier.semantics {heading() },
                                text = stringResource(R.string.map_instruction_pin_placed),
                                style = MaterialTheme.typography.titleLarge,
                                color = colorScheme.onSurface
                            )
                        }

                        else -> {}
                    }
                }
            }

            AddressSearchBar(
                query = searchQuery,
                results = searchResults,
                isLoading = isSearchLoading,
                onQueryChanged = { addressSearchViewModel.onQueryChanged(it) },
                onAddressSelected = { address ->
                    flyToOnNextChange = true
                    mapViewModel.placePin(address.lon, address.lat)
                },
                onClearResults = { addressSearchViewModel.clearResults() },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // BottomCard that changes content depending on the state (see enum class in MapViewModel)
        if (uiState.screenState != MapScreenState.NoSelection) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .onGloballyPositioned { coordinates ->
                        // Converts from pixels to dp so we can use the value as padding
                        bottomCardHeight = with(density) { coordinates.size.height.toDp() }
                    }
            ) {
                BottomCard(
                    state = uiState.screenState,
                    uiState = uiState,
                    // Lambda that defines what should happen when data is loaded
                    onLoadData = {
                        clickedCoordinates?.let { point ->
                            // Adds long and lat to UiState so other screens do not show the error screen (noLocationBox)
                            mapViewModel.confirmSelection(point.longitude(), point.latitude())

                            // Calls the ViewModel for the climate screen
                            climateStatisticsViewModel.loadClimateStatistics(
                                point.longitude(),
                                point.latitude()
                            )
                            climateStatisticsViewModel.setLocationName(uiState.location)

                            // Loads plants through ClimateProfileRepository
                            plantViewModel.loadPlants(point.longitude(), point.latitude())
                            plantViewModel.setLocationName(uiState.location)

                            mapViewModel.markDataLoaded()  // Sets screenState to DataLoaded
                        }
                    },
                    // Lambda that defines what should happen when the user selects a new position
                    onReset = { mapViewModel.clearLocation()} , // Resets uiState to default

                    // Defines navigation routes to climate statistics, plants, and measures
                    onNavigateToClimate = { navController.navigate("climate_statistics") },
                    onNavigateToPlants = { navController.navigate("recommended_plants") },
                    onNavigateToBees = { navController.navigate("bees") },
                    onNavigateToActions = { navController.navigate("actions") },
                )
            }
        }

        // Zoom buttons: automatically lifted above BottomCard by reading the measured height
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = bottomCardHeight + 16.dp), //
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = {
                    val currentZoom = mapViewportState.cameraState?.zoom
                    mapViewportState.setCameraOptions {
                        zoom(currentZoom?.plus(1.0))
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.map_zoom_in),
                )
            }

            // Zoom out button
            Button(
                onClick = {
                    val currentZoom = mapViewportState.cameraState?.zoom
                    mapViewportState.setCameraOptions {
                        zoom(currentZoom?.minus(1.0))
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = stringResource(R.string.map_zoom_out),
                )
            }
        }
    }
}

@Composable
private fun BottomCard(
    state: MapScreenState,
    uiState: MapUiState,
    onLoadData: () -> Unit,
    onReset: () -> Unit,
    onNavigateToClimate: () -> Unit,
    onNavigateToPlants: () -> Unit,
    onNavigateToBees: () -> Unit,
    onNavigateToActions: () -> Unit,
) {
  
    val scrollState = rememberScrollState()
   
    // Description of action (climate) that can be read by a screen reader
    val climateDestinationName = stringResource(R.string.destination_climate_statistics)
    val climateClickLabel = stringResource(
        R.string.hamburgermenu_items, // "to navigate to..."
        climateDestinationName
    )

    // Description of action (plants) that can be read by a screen reader
    val plantsDestinationName = stringResource(R.string.destination_recommended_plants)
    val plantsClickLabel = stringResource(
        R.string.hamburgermenu_items,
        plantsDestinationName
    )

    // Description of action (bee measures) that can be read by a screen reader
    val beesDestinationName = stringResource(R.string.destination_bees)
    val beesClickLabel = stringResource(
        R.string.hamburgermenu_items,
        beesDestinationName
    )

    // Description of action (torrential rain measures) that can be read by a screen reader
    val actionsDestinationName = stringResource(R.string.destination_actions)
    val actionsClickLabel = stringResource(
        R.string.hamburgermenu_items,
        actionsDestinationName
    )

    val dataLoadedFocusRequester = remember { FocusRequester() }

    // When state is loaded, move focus
    LaunchedEffect(state) {
        if (state == MapScreenState.DataLoaded) {
            dataLoadedFocusRequester.requestFocus()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            Text(stringResource(R.string.map_loading))
            return@Column
        }

        if (!uiState.errorMessage.isNullOrEmpty()) {
            Text(text = uiState.errorMessage)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onReset) { Text(stringResource(R.string.map_change_position)) }
            return@Column
        }

        when (state) {
            // Step 1: Pin is set, shows the place name and lets the user confirm
            MapScreenState.PinPlaced -> {
                Text(
                    text = stringResource(R.string.map_location_selected),
                    color = colorScheme.onSurface)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable (
                        onClickLabel = stringResource(R.string.change_location),
                        // Resets the screen, like "change position" does now:
                        onClick = { onReset() }
                    )
                ) {
                    Text(
                        text = uiState.location ?: "",
                        style = MaterialTheme.typography.titleLarge, color = colorScheme.onSurface
                    )
                    // Symbol for changing location:
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit_location),
                        contentDescription = null, // Set to null because Row already has onClickLabel
                        modifier = Modifier.size(24.dp),
                        tint = colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.map_pin_placed_description),
                    style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onLoadData,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.map_load_data_button, uiState.location ?: ""),
                        color = colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.map_change_position), color = colorScheme.onSurface
                    )
                }
            }


            // Step 2: Data has been fetched, shows shortcuts to the other screens

            MapScreenState.DataLoaded -> {
                Text(
                    text = stringResource(R.string.map_location_selected),
                    color = colorScheme.onSurface,
                    modifier = Modifier
                        .focusRequester(dataLoadedFocusRequester) //Semantics: when data is loaded, start reading from this place
                        .focusable()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable(
                        onClickLabel = stringResource(R.string.change_location),
                        onClick = { onReset() }
                    )
                ) {
                    Text(
                        text = uiState.location ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                    // Symbol for changing location:
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit_location),
                        contentDescription = null, // Set to null because Row already has onClickLabel
                        modifier = Modifier.size(24.dp),
                        tint = colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.map_navigate_description))

                Spacer(modifier = Modifier.height(8.dp))

                // Shortcut buttons to climate/plant/bee/measures screen
                // Two buttons per Row
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Button to climate screen
                        Button(
                            onClick = onNavigateToClimate,
                            modifier = Modifier.semantics {
                                onClick(label = climateClickLabel, action = null)
                            }
                        ) {
                            Icon(painter = painterResource(id = R.drawable.ic_monitoring), contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.map_button_climate))
                        }
                        // Button to plants screen
                        Button(onClick = onNavigateToPlants,
                            modifier = Modifier.semantics {
                                onClick(label = plantsClickLabel, action = null)
                            }
                        ) {
                            Icon(Icons.Default.LocalFlorist, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.map_button_plants))
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Button to bees screen
                        Button(
                            onClick = onNavigateToBees,
                            modifier = Modifier.semantics {
                                onClick(label = beesClickLabel, action = null)
                            }
                        ) {
                            Icon(painter = painterResource(id = R.drawable.ic_bie), contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.map_button_bees))
                        }
                        Button(
                            onClick = onNavigateToActions,
                            modifier = Modifier.semantics {
                                onClick(label = actionsClickLabel, action = null)
                            }
                        ) {
                            Icon(painter = painterResource(id = R.drawable.ic_rain), contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.map_button_actions))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.map_change_position), color = colorScheme.onSurface)
                }
            }
            MapScreenState.NoSelection -> { /* Show nothing */ }
        }
    }
}