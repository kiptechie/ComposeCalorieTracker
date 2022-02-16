package com.kiptechie.tracker_domain.use_cases

import com.google.common.truth.Truth.assertThat
import com.kiptechie.core.domain.models.ActivityLevel
import com.kiptechie.core.domain.models.Gender
import com.kiptechie.core.domain.models.GoalType
import com.kiptechie.core.domain.models.UserInfo
import com.kiptechie.core.domain.preferences.Preferences
import com.kiptechie.tracker_domain.models.MealType
import com.kiptechie.tracker_domain.models.TrackedFood
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.random.Random

class CalculateMealNutrientsTest {

    private lateinit var calculateMealNutrients: CalculateMealNutrients

    @Before
    fun setUp() {
        val preferences = mockk<Preferences>(relaxed = true)
        every { preferences.loadUserInfo() } returns UserInfo(
            gender = Gender.Male,
            age = 20,
            weight = 80f,
            height = 180,
            activityLevel = ActivityLevel.Medium,
            goalType = GoalType.KeepWeight,
            carbRatio = 0.4f,
            proteinRatio = 0.3f,
            fatRatio = 0.3f
        )
        calculateMealNutrients = CalculateMealNutrients(preferences)
    }

    @Test
    fun `Calories for breakfast properly calculated`() {
        val trackedFoods = (1..30).map {
            TrackedFood(
                name = "name",
                carbs = Random.nextInt(100),
                protein = Random.nextInt(100),
                fat = Random.nextInt(100),
                mealType = MealType.fromString(
                    listOf("breakfast", "lunch", "dinner", "snack").random()
                ),
                imageUrl = null,
                amount = 100,
                date = LocalDate.now(),
                calories = Random.nextInt(2000)
            )
        }
        val result = calculateMealNutrients(trackedFoods)
        val breakFastCalories = result.mealNutrients.values
            .filter { it.mealType is MealType.Breakfast }
            .sumOf { it.calories }
        val expectedBreakFastCalories = trackedFoods
            .filter { it.mealType is MealType.Breakfast }
            .sumOf { it.calories }
        assertThat(breakFastCalories).isEqualTo(expectedBreakFastCalories)
    }

    @Test
    fun `Calories for lunch properly calculated`() {
        val trackedFoods = (1..30).map {
            TrackedFood(
                name = "name",
                carbs = Random.nextInt(100),
                protein = Random.nextInt(100),
                fat = Random.nextInt(100),
                mealType = MealType.fromString(
                    listOf("breakfast", "lunch", "dinner", "snack").random()
                ),
                imageUrl = null,
                amount = 100,
                date = LocalDate.now(),
                calories = Random.nextInt(2000)
            )
        }
        val result = calculateMealNutrients(trackedFoods)
        val breakFastCalories = result.mealNutrients.values
            .filter { it.mealType is MealType.Lunch }
            .sumOf { it.calories }
        val expectedBreakFastCalories = trackedFoods
            .filter { it.mealType is MealType.Lunch }
            .sumOf { it.calories }
        assertThat(breakFastCalories).isEqualTo(expectedBreakFastCalories)
    }

    @Test
    fun `Calories for dinner properly calculated`() {
        val trackedFoods = (1..30).map {
            TrackedFood(
                name = "name",
                carbs = Random.nextInt(100),
                protein = Random.nextInt(100),
                fat = Random.nextInt(100),
                mealType = MealType.fromString(
                    listOf("breakfast", "lunch", "dinner", "snack").random()
                ),
                imageUrl = null,
                amount = 100,
                date = LocalDate.now(),
                calories = Random.nextInt(2000)
            )
        }
        val result = calculateMealNutrients(trackedFoods)
        val breakFastCalories = result.mealNutrients.values
            .filter { it.mealType is MealType.Dinner }
            .sumOf { it.calories }
        val expectedBreakFastCalories = trackedFoods
            .filter { it.mealType is MealType.Dinner }
            .sumOf { it.calories }
        assertThat(breakFastCalories).isEqualTo(expectedBreakFastCalories)
    }

    @Test
    fun `Calories for snack properly calculated`() {
        val trackedFoods = (1..30).map {
            TrackedFood(
                name = "name",
                carbs = Random.nextInt(100),
                protein = Random.nextInt(100),
                fat = Random.nextInt(100),
                mealType = MealType.fromString(
                    listOf("breakfast", "lunch", "dinner", "snack").random()
                ),
                imageUrl = null,
                amount = 100,
                date = LocalDate.now(),
                calories = Random.nextInt(2000)
            )
        }
        val result = calculateMealNutrients(trackedFoods)
        val breakFastCalories = result.mealNutrients.values
            .filter { it.mealType is MealType.Snack }
            .sumOf { it.calories }
        val expectedBreakFastCalories = trackedFoods
            .filter { it.mealType is MealType.Snack }
            .sumOf { it.calories }
        assertThat(breakFastCalories).isEqualTo(expectedBreakFastCalories)
    }
}