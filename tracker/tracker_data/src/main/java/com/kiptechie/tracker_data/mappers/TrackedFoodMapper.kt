package com.kiptechie.tracker_data.mappers

import com.kiptechie.tracker_data.local.entities.TrackedFoodEntity
import com.kiptechie.tracker_domain.models.MealType
import com.kiptechie.tracker_domain.models.TrackedFood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.time.LocalDate

fun TrackedFoodEntity.toTrackedFood(): TrackedFood {
    return TrackedFood(
        name = name,
        carbs = carbs,
        protein = protein,
        fat = fat,
        imageUrl = imageUrl,
        mealType = MealType.fromString(type),
        amount = amount,
        date = LocalDate.of(year, month, dayOfMonth),
        calories = calories,
        id = id
    )
}

fun List<TrackedFoodEntity>.toTrackedFoodList(): List<TrackedFood> {
    return mapNotNull { trackedFoodEntity ->
        trackedFoodEntity.toTrackedFood()
    }
}

fun Flow<List<TrackedFoodEntity>>.toTrackedFoodFlow(): Flow<List<TrackedFood>> {
    return mapNotNull { entities -> entities.toTrackedFoodList() }
}

fun TrackedFood.toTrackedFoodEntity(): TrackedFoodEntity {
    return TrackedFoodEntity(
        name = name,
        carbs = carbs,
        protein = protein,
        fat = fat,
        imageUrl = imageUrl,
        type = mealType.name,
        amount = amount,
        dayOfMonth = date.dayOfMonth,
        month = date.monthValue,
        year = date.year,
        calories = calories,
        id = id
    )
}