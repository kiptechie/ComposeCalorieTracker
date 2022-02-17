package com.kiptechie.tracker_presentation.tracker_overview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.kiptechie.core.R
import com.kiptechie.core_ui.LocalSpacing
import com.kiptechie.tracker_presentation.tracker_overview.components.*

@ExperimentalCoilApi
@Composable
fun TrackerOverviewScreen(
    onNavigateToSearch: (String, Int, Int, Int) -> Unit,
    viewModel: TrackerOverviewViewModel = hiltViewModel()
) {
    val dimens = LocalSpacing.current
    val state = viewModel.state
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = dimens.medium),
        content = {
            item {
                NutrientsHeader(state = state)
                Spacer(modifier = Modifier.height(dimens.medium))
                DaySelector(
                    date = state.date,
                    onPreviousDateClick = {
                        viewModel.onEvent(TrackerOverviewEvent.OnPreviousDayClick)
                    },
                    onNextDayClick = {
                        viewModel.onEvent(TrackerOverviewEvent.OnNextDayClick)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.medium)
                )
                Spacer(modifier = Modifier.height(dimens.medium))
            }
            items(state.meals) { meal ->
                ExpandableMeal(
                    meal = meal,
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimens.small)
                        ) {
                            val foods = state.trackedFoods.filter {
                                it.mealType == meal.mealType
                            }
                            foods.forEach { food ->
                                TrackedFoodItem(
                                    trackedFood = food,
                                    onDeleteClick = {
                                        viewModel.onEvent(
                                            TrackerOverviewEvent.OnDeleteTrackedFoodClick(food)
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.height(dimens.medium))
                            }
                            AddButton(
                                text = stringResource(
                                    id = R.string.add_meal,
                                    meal.name.asString(context)
                                ),
                                onClick = {
                                    onNavigateToSearch(
                                        meal.name.asString(context),
                                        state.date.dayOfMonth,
                                        state.date.monthValue,
                                        state.date.year
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    onToggleClick = {
                        viewModel.onEvent(TrackerOverviewEvent.OnToggleMealClick(meal))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}