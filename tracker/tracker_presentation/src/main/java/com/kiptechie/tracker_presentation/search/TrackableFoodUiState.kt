package com.kiptechie.tracker_presentation.search

import com.kiptechie.tracker_domain.models.TrackableFood

data class TrackableFoodUiState(
    val food: TrackableFood,
    val isExpanded: Boolean = false,
    val amount: String = ""
)
