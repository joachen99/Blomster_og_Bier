package in2000.gruppe8.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import in2000.gruppe8.data.preferences.UserPreferencesRepository
import in2000.gruppe8.data.climate.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// Data class for UI state
data class MapUiState(
    val location: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedLongitude: Double? = null,  // Stores selected longitude so other screens can access it (e.g. PlantScreen)
    val selectedLatitude: Double? = null,    // Stores selected latitude, corresponding to the above

    // These are used to remember the screen state across screens
    val screenState: MapScreenState = MapScreenState.NoSelection,  // Which phase the user is in on the map screen
    val pinLongitude: Double? = null,       // Placement of the pin on the map (regardless of whether the selection is confirmed)
    val pinLatitude: Double? = null         // Placement of the pin on the map (regardless of whether the selection is confirmed)
)

// The states for MapScreen, placed in the ViewModel so they are remembered when the user navigates away from MapScreen
enum class MapScreenState {
    NoSelection,
    PinPlaced,
    DataLoaded
}

// Consider changing the name to MapViewModel, since it concerns the map screen
class MapViewModel (
    private val weatherRepository: WeatherRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _mapState = MutableStateFlow(MapUiState())
    val mapState = _mapState.asStateFlow()


   

    // This init-block reads saved location and restores it in the _mapState, so data survives the app being closed
    init {
        viewModelScope.launch {
            val saved = userPreferencesRepository.savedLocation.first()
            if (saved != null) {
                _mapState.update { current ->
                    current.copy(
                        location = saved.name,
                        selectedLongitude = saved.longitude,
                        selectedLatitude = saved.latitude,
                        pinLongitude = saved.longitude,
                        pinLatitude = saved.latitude,
                        screenState = MapScreenState.DataLoaded
                    )
                }
            }
        }
    }

    // Updates the place name in UIState when the pin is placed on the map

    fun getName(
        longitude: Double,
        latitude: Double,
    ) {
        _mapState.update { current ->
            current.copy(
                isLoading = true,
                errorMessage = null,
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            // Moved the repository call outside .update, this was done during debugging of MVP 1.0
            try {
                val location = weatherRepository.getName(longitude, latitude)
                _mapState.update { current ->
                    current.copy(
                        location = location,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                println("ERROR: ${e.message}")

                _mapState.update { current ->
                    current.copy(
                        location = null,
                        isLoading = false,
                        errorMessage = "Kunne ikke finne sted. Sjekk om du har internett"
                    )
                }
            }
        }
    }

    // Updates MapUiState with longitude and latitude when the user presses "Load data"
    fun confirmSelection(longitude: Double, latitude: Double) {
        _mapState.update{current ->
            current.copy(
                selectedLongitude = longitude ,
                selectedLatitude  = latitude
            )
        }
        // Saves location in DataStore when user loads data for selected location
        val locationName = _mapState.value.location
        if (locationName != null) {
            viewModelScope.launch {
                userPreferencesRepository.saveLocation(longitude, latitude, locationName)
            }
        }
    }

    // Resets UIState when the user presses "Choose another location"
    fun clearLocation() {
        _mapState.update { MapUiState() }

        // Deletes data also from DataStore when user chooses a new loaction
        viewModelScope.launch {
            userPreferencesRepository.clearLocation()
        }
    }

    // Stores pin coordinates and sets screen state to PinPlaced
    fun placePin(longitude: Double, latitude: Double) {
        _mapState.update { current ->
            current.copy(
                pinLongitude = longitude,
                pinLatitude = latitude,
                screenState = MapScreenState.PinPlaced,

                // Resets place name and confirmed selection, so old data is not shown when a new pin is placed
                location = null,
                selectedLongitude = null,
                selectedLatitude = null
            )
        }
    }

    // Sets screen state to DataLoaded after the user confirms the selection
    fun markDataLoaded() {
        _mapState.update { current ->
            current.copy(screenState = MapScreenState.DataLoaded)
        }
    }

}