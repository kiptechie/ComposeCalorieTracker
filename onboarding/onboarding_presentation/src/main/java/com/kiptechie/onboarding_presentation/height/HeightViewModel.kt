package com.kiptechie.onboarding_presentation.height

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiptechie.core.R
import com.kiptechie.core.domain.preferences.Preferences
import com.kiptechie.core.domain.use_cases.FilterOutDigits
import com.kiptechie.core.navigation.Route
import com.kiptechie.core.util.UiEvent
import com.kiptechie.core.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeightViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val preferences: Preferences,
    private val filterOutDigits: FilterOutDigits
) : ViewModel() {

    var height by mutableStateOf("180")
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val userInfo = preferences.loadUserInfo()
            val heightFromPreferences = userInfo.height.toString()
            val heightFromSavedState =
                savedStateHandle.get<String>(KEY_HEIGHT) ?: heightFromPreferences
            height = heightFromSavedState
        }
    }

    fun onHeightEnter(height: String) {
        if (height.length <= 3) {
            this.height = filterOutDigits(height)
            savedStateHandle.set(KEY_HEIGHT, this.height)
        }
    }

    fun onNextClick() {
        viewModelScope.launch {
            val heightNumber = height.toIntOrNull() ?: kotlin.run {
                _uiEvent.send(
                    UiEvent.ShowSnackBar(
                        UiText.StringResource(R.string.error_height_cant_be_empty)
                    )
                )
                return@launch
            }
            preferences.saveHeight(heightNumber)
            _uiEvent.send(UiEvent.Navigate(Route.WEIGHT))
        }
    }

    companion object {
        const val KEY_HEIGHT = "height"
    }
}