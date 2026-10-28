package in2000.gruppe8.ui.actions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import in2000.gruppe8.data.recommendation.ActionsDataSource
import in2000.gruppe8.data.recommendation.BeeDataSource
import in2000.gruppe8.data.recommendation.RecommendationRepository
import in2000.gruppe8.model.actions.ActionInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class ActionsUiState(
    val action: List<ActionInfo> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ActionsViewModel(
    actionsDataSource: ActionsDataSource,
    beeDataSource: BeeDataSource
) : ViewModel(){
    private val recommendationRepository: RecommendationRepository
            = RecommendationRepository(actionsDataSource, beeDataSource)

    private val _actionsUiState = MutableStateFlow(ActionsUiState())
    val actionsUiState = _actionsUiState.asStateFlow()

    init {
        getMoreActionInfo()
    }

    fun getMoreActionInfo() {
        viewModelScope.launch(Dispatchers.IO){
            try{
                val actions = recommendationRepository.getActions()
                _actionsUiState.value = ActionsUiState(
                    action = actions,
                    isLoading = false,
                    error = null
                )
            }catch (e: Exception){
                _actionsUiState.value = ActionsUiState(
                    action = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Could not get recommended actions"
                )
            }
        }
    }
}