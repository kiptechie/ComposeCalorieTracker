package com.kiptechie.onboarding_presentation.gender

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiptechie.core.domain.models.Gender
import com.kiptechie.core.domain.preferences.Preferences
import com.kiptechie.core.navigation.Route
import com.kiptechie.core.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenderViewModel @Inject constructor(
    private val preferences: Preferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val keySelectedGender = "selected-gender"

    var selectedGender by mutableStateOf<Gender>(Gender.Female)
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val userInfo = preferences.loadUserInfo()
            val genderNameFromPreferences = userInfo.gender.name
            val genderName =
                savedStateHandle.get<String>(keySelectedGender) ?: genderNameFromPreferences
            selectedGender =
                Gender.fromString(genderName)
        }
    }

    fun onGenderClick(gender: Gender) {
        selectedGender = gender
        savedStateHandle.set(keySelectedGender, gender.name)
    }

    fun onNextClick() {
        viewModelScope.launch {
            preferences.saveGender(gender = selectedGender)
            _uiEvent.send(UiEvent.Navigate(Route.AGE))
        }
    }
}