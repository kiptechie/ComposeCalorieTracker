package com.kiptechie.mycalorietracker.repository

import com.kiptechie.tracker_domain.models.TrackableFood
import com.kiptechie.tracker_domain.models.TrackedFood
import com.kiptechie.tracker_domain.repository.TrackerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalDate
import kotlin.random.Random

class FakeTrackerRepository : TrackerRepository {

    var shouldReturnError: Boolean = false

    private val trackedFoods = mutableListOf<TrackedFood>()
    var searchResults = listOf<TrackableFood>()

    private val getFoodsForDateFLow = MutableSharedFlow<List<TrackedFood>>(replay = 1)

    override suspend fun searchFood(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<List<TrackableFood>> {
        return if (shouldReturnError) {
            Result.failure(Throwable())
        } else {
            Result.success(searchResults)
        }
    }

    override suspend fun insertTrackedFood(food: TrackedFood) {
        trackedFoods.add(food.copy(id = Random.nextInt()))
        getFoodsForDateFLow.emit(trackedFoods)
    }

    override suspend fun deleteTrackedFood(food: TrackedFood) {
        trackedFoods.remove(food)
        getFoodsForDateFLow.emit(trackedFoods)
    }

    override fun getFoodForDate(localDate: LocalDate): Flow<List<TrackedFood>> {
        return getFoodsForDateFLow
    }

}