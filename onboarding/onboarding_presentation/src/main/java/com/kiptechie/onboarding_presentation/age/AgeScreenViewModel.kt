package com.kiptechie.onboarding_presentation.age

import android.app.Application
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
class AgeScreenViewModel @Inject constructor(
    private val preferences: Preferences,
    private val savedStateHandle: SavedStateHandle,
    private val filterOutDigits: FilterOutDigits,
    private val app: Application
) : ViewModel() {

    private val keyAge = "age"

    var age by mutableStateOf("20")
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val userInfo = preferences.loadUserInfo()
            var preferenceAge = userInfo.age.toString()
            if (preferenceAge == "-1") {
                preferenceAge = "20"
            }
            val savedStateAge = savedStateHandle.get<String>(keyAge) ?: preferenceAge
            age = savedStateAge
        }
    }

    fun onAgeEnter(age: String) {
        if (age.length <= 3) {
            this.age = filterOutDigits(age)
            savedStateHandle.set(keyAge, this.age)
        }
    }

    fun onNextClick() {
        viewModelScope.launch {
            val ageNumber = age.toIntOrNull() ?: kotlin.run {
                _uiEvent.send(
                    UiEvent.ShowSnackBar(
                        message = UiText.StringResource(R.string.error_age_cant_be_empty)
                    )
                )
                return@launch
            }
            preferences.saveAge(ageNumber)
            savedStateHandle.set(keyAge, ageNumber.toString())
            _uiEvent.send(UiEvent.Navigate(Route.HEIGHT))
        }
    }

}