package in2000.gruppe8.ui.bee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import in2000.gruppe8.data.recommendation.ActionsDataSource
import in2000.gruppe8.data.recommendation.BeeDataSource
import in2000.gruppe8.data.recommendation.RecommendationRepository
import in2000.gruppe8.model.bees.BeeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class BeeUiState(
    val bee: List<BeeInfo> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class BeeViewModel(
    actionsDataSource: ActionsDataSource,
    beeDataSource: BeeDataSource
) : ViewModel(){
    private val recommendationRepository: RecommendationRepository
            = RecommendationRepository(actionsDataSource, beeDataSource)

    private val _beeUiState = MutableStateFlow(BeeUiState())
    val beeUiState = _beeUiState.asStateFlow()

    init {
        getMoreBeeInfo()
    }

    fun getMoreBeeInfo() {
        viewModelScope.launch(Dispatchers.IO){
            try{
                val bees = recommendationRepository.getBees()
                _beeUiState.value = BeeUiState(
                    bee = bees,
                    isLoading = false,
                    error = null
                )
            }catch (e: Exception){
                _beeUiState.value = BeeUiState(
                    bee = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Could not get bee actions"
                )
            }
        }
    }
}