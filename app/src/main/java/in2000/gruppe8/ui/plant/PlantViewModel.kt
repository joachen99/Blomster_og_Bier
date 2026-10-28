
package in2000.gruppe8.ui.plant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import in2000.gruppe8.data.plants.PlantEntry
import in2000.gruppe8.domain.ClimateProfileRepository
import in2000.gruppe8.model.plants.ClimateSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI state for the new plant screen
data class PlantUiState(
    val plants: List<PlantEntry> = emptyList(),
    val climateSignal: ClimateSignal? = null,
    val location: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// ViewModel that fetches climate-adapted plants from ClimateProfileRepository
class PlantViewModel(
    private val climateProfileRepository: ClimateProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlantUiState())
    val uiState = _uiState.asStateFlow()

    // Called from MapScreen when the user confirms the position with "Load data"
    fun loadPlants(longitude: Double, latitude: Double) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (signal, plants) = climateProfileRepository.fetchPlants(longitude, latitude)
                _uiState.update { it.copy(plants = plants, climateSignal = signal, isLoading = false) }
            } catch (e: Exception) {
                println("ERROR: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Kunne ikke hente planter"
                    )
                }
            }
        }
    }

    // Sets the location name so it can be displayed in the header
    fun setLocationName(name: String?) {
        _uiState.update { it.copy(location = name) }
    }
}