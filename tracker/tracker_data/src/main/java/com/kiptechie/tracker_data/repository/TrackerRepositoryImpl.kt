package com.kiptechie.tracker_data.repository

import android.util.Log
import com.kiptechie.tracker_data.local.TrackerDao
import com.kiptechie.tracker_data.mappers.toTrackableFoodList
import com.kiptechie.tracker_data.mappers.toTrackedFoodEntity
import com.kiptechie.tracker_data.mappers.toTrackedFoodFlow
import com.kiptechie.tracker_data.remote.OpenFoodApi
import com.kiptechie.tracker_domain.models.TrackableFood
import com.kiptechie.tracker_domain.models.TrackedFood
import com.kiptechie.tracker_domain.repository.TrackerRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TrackerRepositoryImpl(
    private val dao: TrackerDao,
    private val api: OpenFoodApi
) : TrackerRepository {

    companion object {
        const val TAG = "TrackerRepositoryImpl"
    }

    override suspend fun searchFood(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<List<TrackableFood>> {
        return try {
            val searchDto = api.searchFood(query, page, pageSize)
            Result.success(searchDto.products.toTrackableFoodList())
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            Result.failure(e)
        }
    }

    override suspend fun insertTrackedFood(food: TrackedFood) {
        dao.insert(food.toTrackedFoodEntity())
    }

    override suspend fun deleteTrackedFood(food: TrackedFood) {
        dao.delete(food.toTrackedFoodEntity())
    }

    override fun getFoodForDate(localDate: LocalDate): Flow<List<TrackedFood>> {
        return dao.getFoodsForDate(
            day = localDate.dayOfMonth,
            month = localDate.monthValue,
            year = localDate.year
        ).toTrackedFoodFlow()
    }
}