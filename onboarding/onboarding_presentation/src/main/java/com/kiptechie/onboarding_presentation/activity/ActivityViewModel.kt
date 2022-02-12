package com.kiptechie.onboarding_presentation.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiptechie.core.domain.models.ActivityLevel
import com.kiptechie.core.domain.preferences.Preferences
import com.kiptechie.core.navigation.Route
import com.kiptechie.core.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val preferences: Preferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var selectedActivityLevel by mutableStateOf<ActivityLevel>(
        ActivityLevel.Medium
    )
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val userInfo = preferences.loadUserInfo()
            val activityLevelFromPreferences = userInfo.activityLevel.name
            val activityLevelFromSavedState =
                savedStateHandle.get<String>(KEY_ACTIVITY_LEVEL) ?: activityLevelFromPreferences
            selectedActivityLevel = ActivityLevel.fromString(activityLevelFromSavedState)
        }
    }

    fun onActivityLevelSelect(activityLevel: ActivityLevel) {
        selectedActivityLevel = activityLevel
        savedStateHandle.set(KEY_ACTIVITY_LEVEL, activityLevel.name)
    }

    fun onNextClick() {
        viewModelScope.launch {
            preferences.saveActivityLevel(selectedActivityLevel)
            _uiEvent.send(UiEvent.Navigate(Route.GOAL))
        }
    }

    companion object {
        const val KEY_ACTIVITY_LEVEL = "activity-level"
    }
}