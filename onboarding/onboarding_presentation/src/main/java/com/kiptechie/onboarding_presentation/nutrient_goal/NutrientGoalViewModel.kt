package com.kiptechie.onboarding_presentation.nutrient_goal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiptechie.core.domain.models.getPercent
import com.kiptechie.core.domain.preferences.Preferences
import com.kiptechie.core.domain.use_cases.FilterOutDigits
import com.kiptechie.core.util.UiEvent
import com.kiptechie.onboarding_domain.use_cases.ValidateNutrients
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutrientGoalViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val preferences: Preferences,
    private val filterOutDigits: FilterOutDigits,
    private val validateNutrients: ValidateNutrients
) : ViewModel() {

    var state by mutableStateOf(NutrientGoalState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val userInfo = preferences.loadUserInfo()
            val carbsP = userInfo.carbRatio.getPercent().toString()
            val proteinsP = userInfo.proteinRatio.getPercent().toString()
            val fatsP = userInfo.fatRatio.getPercent().toString()
            val carbs = savedStateHandle.get<String>(KEY_CARBS) ?: carbsP
            val proteins = savedStateHandle.get<String>(KEY_PROTEIN) ?: proteinsP
            val fats = savedStateHandle.get<String>(KEY_FAT) ?: fatsP
            state = NutrientGoalState(
                carbsRatio = carbs,
                proteinRatio = proteins,
                fatRatio = fats
            )
        }
    }

    fun onEvent(event: NutrientGoalEvent) {
        when (event) {
            is NutrientGoalEvent.OnCarbRatioEnter -> {
                state = state.copy(
                    carbsRatio = filterOutDigits(event.ratio)
                )
                savedStateHandle.set(KEY_CARBS, state.carbsRatio)
            }
            is NutrientGoalEvent.OnProteinRatioEnter -> {
                state = state.copy(
                    proteinRatio = filterOutDigits(event.ratio)
                )
                savedStateHandle.set(KEY_PROTEIN, state.proteinRatio)
            }
            is NutrientGoalEvent.OnFatRatioEnter -> {
                state = state.copy(
                    fatRatio = filterOutDigits(event.ratio)
                )
                savedStateHandle.set(KEY_FAT, state.fatRatio)
            }
            is NutrientGoalEvent.OnNextClick -> {
                val result = validateNutrients(
                    carbsRatioText = state.carbsRatio,
                    proteinRatioText = state.proteinRatio,
                    fatRatioText = state.fatRatio
                )
                when (result) {
                    is ValidateNutrients.Result.Success -> {
                        viewModelScope.launch {
                            preferences.saveCarbRatio(result.carbsRatio)
                            preferences.saveProteinRatio(result.proteinRatio)
                            preferences.saveFatRatio(result.fatRatio)
                            _uiEvent.send(UiEvent.Success)
                        }
                    }
                    is ValidateNutrients.Result.Error -> {
                        viewModelScope.launch {
                            _uiEvent.send(UiEvent.ShowSnackBar(result.message))
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_PROTEIN = "protein"
        const val KEY_FAT = "fat"
        const val KEY_CARBS = "carbs"
    }
}