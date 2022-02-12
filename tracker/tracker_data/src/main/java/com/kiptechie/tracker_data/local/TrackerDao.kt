package com.kiptechie.tracker_data.local

import androidx.room.*
import com.kiptechie.tracker_data.local.entities.TrackedFoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trackedFoodEntity: TrackedFoodEntity)

    @Delete
    suspend fun delete(trackedFoodEntity: TrackedFoodEntity)

    @Query(
        """
            SELECT *
            FROM tracked_foods
            WHERE dayOfMonth = :day AND month =:month AND year =:year
            """
    )
    fun getFoodsForDate(day: Int, month: Int, year: Int): Flow<List<TrackedFoodEntity>>

}