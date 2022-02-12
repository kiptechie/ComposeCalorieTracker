package com.kiptechie.tracker_domain.use_cases

import com.kiptechie.tracker_domain.models.TrackedFood
import com.kiptechie.tracker_domain.repository.TrackerRepository

class DeleteTrackedFood(
    private val repository: TrackerRepository
) {
    suspend operator fun invoke(trackedFood: TrackedFood) {
        return repository.deleteTrackedFood(trackedFood)
    }
}