package com.kiptechie.mycalorietracker.navigation

import androidx.navigation.NavController
import com.kiptechie.core.util.UiEvent

fun NavController.navigate(event: UiEvent.Navigate) {
    this.navigate(event.route)
}