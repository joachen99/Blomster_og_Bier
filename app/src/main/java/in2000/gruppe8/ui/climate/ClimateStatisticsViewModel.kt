package in2000.gruppe8.ui.climate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import in2000.gruppe8.data.climate.WeatherRepository
import in2000.gruppe8.domain.ClimateProfileRepository
import in2000.gruppe8.model.plants.ClimateSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClimateStatisticsUiState(
    val earlySummerPrecipitation: Int? = null,
    val lateSummerPrecipitation: Int? = null,
    val yearlyHeavyRain: List<Pair<Int, Double>> = emptyList(),
    val yearlyHeavyRainRegression: List<Pair<Int, Double>> = emptyList(),
    val summerPrecipitationTrend: ClimateSignal = ClimateSignal.STABLE,
    val heavyRainTrend: ClimateSignal = ClimateSignal.STABLE,
    val yearlyPrecipitation: List<Pair<Int, Double>> = emptyList(),
    val yearlyPrecipitationRegression: List<Pair<Int, Double>> = emptyList(),
    val location: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class ClimateStatisticsViewModel(
    weatherRepository: WeatherRepository
) : ViewModel() {
    private val climateProfileRepository: ClimateProfileRepository
    = ClimateProfileRepository(weatherRepository)

    private val _climateStatisticsUiState = MutableStateFlow(ClimateStatisticsUiState())
    val climateStatisticsUiState = _climateStatisticsUiState.asStateFlow()

    fun setLocationName(name: String?) {
        _climateStatisticsUiState.update { it.copy(location = name) }
    }
    fun loadClimateStatistics(long: Double, lat: Double) {
        _climateStatisticsUiState.update {
            ClimateStatisticsUiState(
                earlySummerPrecipitation = null,
                lateSummerPrecipitation = null,
                yearlyHeavyRain = emptyList(),
                yearlyHeavyRainRegression = emptyList(),
                summerPrecipitationTrend = ClimateSignal.STABLE,
                heavyRainTrend = ClimateSignal.STABLE,
                yearlyPrecipitation = emptyList(),
                yearlyPrecipitationRegression = emptyList(),
                location = null,
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            var earlySummerPrecipitation: Int? = null
            var lateSummerPrecipitation: Int? = null
            var yearlyHeavyRain: List<Pair<Int, Double>> = emptyList()
            var yearlyHeavyRainRegression: List<Pair<Int, Double>> = emptyList()
            var summerPrecipitationTrend = ClimateSignal.STABLE
            var heavyRainTrend = ClimateSignal.STABLE
            var yearlyPrecipitation: List<Pair<Int, Double>> = emptyList()
            var yearlyPrecipitationRegression: List<Pair<Int, Double>> = emptyList()
            var errorMessage: String? = null

            try {
                try {
                    earlySummerPrecipitation =
                        climateProfileRepository.getEarlySummerPrecipitation(long, lat)
                } catch (e: Exception) {
                        errorMessage =
                            e.message ?: "Could not get early summer precipitation"
                }

                try {
                    lateSummerPrecipitation =
                        climateProfileRepository.getLateSummerPrecipitation(long, lat)
                } catch (e: Exception) {
                    if (errorMessage == null) {
                        errorMessage =
                            e.message ?: "Could not get late summer precipitation"
                    }
                }

                try {
                    yearlyHeavyRain =
                        climateProfileRepository.getHeavyRain(long, lat)
                } catch (e: Exception) {
                    if (errorMessage == null) {
                        errorMessage =
                            e.message ?: "Could not get yearly heavy rain"
                    }
                }

                try {
                    summerPrecipitationTrend =
                        climateProfileRepository.calculateSummerPrecipitationTrend(long, lat)
                } catch (e: Exception) {
                    if (errorMessage == null) {
                        errorMessage =
                            e.message ?: "Could not get yearly heavy rain"
                    }
                }

                try {
                    heavyRainTrend =
                        climateProfileRepository.calculateHeavyRainTrend(long, lat)
                } catch (e: Exception) {
                    if (errorMessage == null) {
                        errorMessage =
                            e.message ?: "Could not get yearly heavy rain"
                    }
                }

                try {
                    yearlyPrecipitation =
                        climateProfileRepository.getYearlyPrecipitation(long, lat)
                } catch (e: Exception) {
                    if (errorMessage == null) {
                        errorMessage =
                            e.message ?: "Could not get yearly heavy rain"
                    }
                }

                try {
                    yearlyPrecipitationRegression =
                        climateProfileRepository.getYearlyPrecipitationRegression(long, lat)
                } catch (e: Exception) {
                    if (errorMessage == null) {
                        errorMessage =
                            e.message ?: "Could not get yearly precipitation regression"
                    }
                }

                try {
                    yearlyHeavyRainRegression =
                        climateProfileRepository.getHeavyRainRegression(long, lat)
                } catch (e: Exception) {
                    if (errorMessage == null) {
                        errorMessage =
                            e.message ?: "Could not get yearly precipitation regression"
                    }
                }

                _climateStatisticsUiState.update { current ->
                    current.copy(
                        earlySummerPrecipitation = earlySummerPrecipitation,
                        lateSummerPrecipitation = lateSummerPrecipitation,
                        yearlyHeavyRain = yearlyHeavyRain,
                        yearlyHeavyRainRegression = yearlyHeavyRainRegression,
                        summerPrecipitationTrend = summerPrecipitationTrend,
                        heavyRainTrend = heavyRainTrend,
                        yearlyPrecipitation = yearlyPrecipitation,
                        yearlyPrecipitationRegression = yearlyPrecipitationRegression,
                        error = errorMessage
                    )
                }
            } finally {
                _climateStatisticsUiState.update { current ->
                    current.copy(isLoading = false)
                }
            }
        }
    }
}