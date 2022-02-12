package com.kiptechie.onboarding_presentation.weight

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiptechie.core.R
import com.kiptechie.core.domain.preferences.Preferences
import com.kiptechie.core.navigation.Route
import com.kiptechie.core.util.UiEvent
import com.kiptechie.core.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val preferences: Preferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var weight by mutableStateOf("80.0")
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val userInfo = preferences.loadUserInfo()
            val weightFromPreferences = userInfo.weight.toString()
            val weightFromSavedState =
                savedStateHandle.get<String>(KEY_WEIGHT) ?: weightFromPreferences
            weight = weightFromSavedState
        }
    }

    fun onWeightEnter(weight: String) {
        if (weight.length <= 5) {
            this.weight = weight
            savedStateHandle.set(KEY_WEIGHT, this.weight)
        }
    }

    fun onNextClick() {
        viewModelScope.launch {
            val weightNumber = weight.toFloatOrNull() ?: kotlin.run {
                _uiEvent.send(
                    UiEvent.ShowSnackBar(
                        UiText.StringResource(R.string.error_weight_cant_be_empty)
                    )
                )
                return@launch
            }
            preferences.saveWeight(weightNumber)
            _uiEvent.send(UiEvent.Navigate(Route.ACTIVITY))
        }
    }

    companion object {
        const val KEY_WEIGHT = "weight"
    }
}