package com.kiptechie.onboarding_presentation.goal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiptechie.core.domain.models.GoalType
import com.kiptechie.core.domain.preferences.Preferences
import com.kiptechie.core.navigation.Route
import com.kiptechie.core.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val preferences: Preferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var selectedGoal by mutableStateOf<GoalType>(
        GoalType.KeepWeight
    )
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val userInfo = preferences.loadUserInfo()
            val goalTypeFromPreferences = userInfo.goalType.name
            val goalTypeFromSavedState =
                savedStateHandle.get<String>(KEY_GOAL_TYPE) ?: goalTypeFromPreferences
            selectedGoal = GoalType.fromString(goalTypeFromSavedState)
        }
    }

    fun onGoalTypeSelect(goalType: GoalType) {
        selectedGoal = goalType
        savedStateHandle.set(KEY_GOAL_TYPE, goalType.name)
    }

    fun onNextClick() {
        viewModelScope.launch {
            preferences.saveGoalType(selectedGoal)
            _uiEvent.send(UiEvent.Navigate(Route.NUTRIENT_GOAL))
        }
    }

    companion object {
        const val KEY_GOAL_TYPE = "goal-type"
    }
}