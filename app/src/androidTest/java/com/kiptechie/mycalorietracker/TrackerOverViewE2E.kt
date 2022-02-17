package com.kiptechie.mycalorietracker

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.google.common.truth.Truth.assertThat
import com.kiptechie.core.domain.models.ActivityLevel
import com.kiptechie.core.domain.models.Gender
import com.kiptechie.core.domain.models.GoalType
import com.kiptechie.core.domain.models.UserInfo
import com.kiptechie.core.domain.preferences.Preferences
import com.kiptechie.core.domain.use_cases.FilterOutDigits
import com.kiptechie.mycalorietracker.navigation.Route
import com.kiptechie.mycalorietracker.repository.FakeTrackerRepository
import com.kiptechie.mycalorietracker.ui.theme.MyCalorieTrackerTheme
import com.kiptechie.tracker_domain.models.TrackableFood
import com.kiptechie.tracker_domain.use_cases.*
import com.kiptechie.tracker_presentation.search.SearchScreen
import com.kiptechie.tracker_presentation.search.SearchViewModel
import com.kiptechie.tracker_presentation.tracker_overview.TrackerOverviewScreen
import com.kiptechie.tracker_presentation.tracker_overview.TrackerOverviewViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.math.roundToInt

@ExperimentalCoilApi
@ExperimentalComposeUiApi
@HiltAndroidTest
class TrackerOverViewE2E {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var fakeTrackerRepository: FakeTrackerRepository
    private lateinit var trackerUseCases: TrackerUseCases
    private lateinit var preferences: Preferences
    private lateinit var trackerOverviewViewModel: TrackerOverviewViewModel
    private lateinit var searchViewModel: SearchViewModel

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        preferences = mockk(relaxed = true)
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
        fakeTrackerRepository = FakeTrackerRepository()
        trackerUseCases = TrackerUseCases(
            calculateMealNutrients = CalculateMealNutrients(preferences),
            deleteTrackedFood = DeleteTrackedFood(fakeTrackerRepository),
            getFoodsForDate = GetFoodsForDate(fakeTrackerRepository),
            searchFood = SearchFood(fakeTrackerRepository),
            trackFood = TrackFood(fakeTrackerRepository)
        )
        trackerOverviewViewModel = TrackerOverviewViewModel(
            preferences,
            trackerUseCases
        )
        searchViewModel = SearchViewModel(
            trackerUseCases,
            filterOutDigits = FilterOutDigits()
        )
        composeRule.setContent {
            MyCalorieTrackerTheme {
                navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                val startDest = Route.TRACKER_OVERVIEW
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState
                ) {
                    NavHost(navController = navController, startDestination = startDest) {
                        composable(Route.TRACKER_OVERVIEW) {
                            TrackerOverviewScreen(
                                onNavigateToSearch = { mealName, day, month, year ->
                                    navController.navigate(
                                        route = Route.SEARCH + "/$mealName" + "/$day" + "/$month" +
                                                "/$year"
                                    )
                                },
                                viewModel = trackerOverviewViewModel
                            )
                        }
                        composable(
                            route = Route.SEARCH + "/{mealName}/{dayOfMonth}/{month}/{year}",
                            arguments = listOf(
                                navArgument("mealName") {
                                    type = NavType.StringType
                                },
                                navArgument("dayOfMonth") {
                                    type = NavType.IntType
                                },
                                navArgument("month") {
                                    type = NavType.IntType
                                },
                                navArgument("year") {
                                    type = NavType.IntType
                                },
                            ),
                        ) {
                            val mealName = it.arguments?.getString("mealName")!!
                            val dayOfMonth = it.arguments?.getInt("dayOfMonth")!!
                            val month = it.arguments?.getInt("month")!!
                            val year = it.arguments?.getInt("year")!!
                            SearchScreen(
                                scaffoldState = scaffoldState,
                                mealName = mealName,
                                dayOfMonth = dayOfMonth,
                                month = month,
                                year = year,
                                onNavigateUp = {
                                    navController.navigateUp()
                                },
                                viewModel = searchViewModel
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun addBreakfast_appearsUnderBreakfast_nutrientsProperlyCalculated() {
        fakeTrackerRepository.searchResults = listOf(
            TrackableFood(
                name = "banana",
                imageUrl = null,
                caloriesPer100g = 150,
                proteinPer100g = 5,
                carbsPer100g = 50,
                fatPer100g = 1
            )
        )
        val addedAmount = 150
        val expectedCalories = (1.5f * 150).roundToInt()
        val expectedCarbs = (1.5f * 50).roundToInt()
        val expectedProtein = (1.5f * 5).roundToInt()
        val expectedFat = (1.5f * 1).roundToInt()
        composeRule
            .onNodeWithText("Add Breakfast")
            .assertDoesNotExist()
        composeRule
            .onNodeWithContentDescription("Breakfast")
            .performClick()
        composeRule
            .onNodeWithText("Add Breakfast")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Add Breakfast")
            .performClick()
        assertThat(
            navController.currentDestination
                ?.route
                ?.startsWith(Route.SEARCH)
        ).isTrue()
        composeRule
            .onNodeWithTag("search_text_field")
            .performTextInput("banana")
        composeRule
            .onNodeWithContentDescription("Search")
            .performClick()
        composeRule
            .onNodeWithText("Carbs")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Amount")
            .performTextInput(addedAmount.toString())
        composeRule
            .onNodeWithContentDescription("Track")
            .performClick()
        assertThat(
            navController.currentDestination
                ?.route
                ?.startsWith(Route.TRACKER_OVERVIEW)
        ).isTrue()
        composeRule
            .onAllNodesWithText(expectedCarbs.toString())
            .onFirst()
            .assertIsDisplayed()
        composeRule
            .onAllNodesWithText(expectedProtein.toString())
            .onFirst()
            .assertIsDisplayed()
        composeRule
            .onAllNodesWithText(expectedFat.toString())
            .onFirst()
            .assertIsDisplayed()
        composeRule
            .onAllNodesWithText(expectedCalories.toString())
            .onFirst()
            .assertIsDisplayed()
    }
}