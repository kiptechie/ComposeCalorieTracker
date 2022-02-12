package com.kiptechie.tracker_data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kiptechie.tracker_data.local.entities.TrackedFoodEntity

@Database(
    entities = [TrackedFoodEntity::class],
    version = 1
)
abstract class TrackerDatabase : RoomDatabase() {

    abstract val dao: TrackerDao

    companion object {
        const val NAME = "tracker-db"
    }

}