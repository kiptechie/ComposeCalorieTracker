package com.kiptechie.tracker_presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.kiptechie.core.R
import com.kiptechie.core.util.UiEvent
import com.kiptechie.core_ui.LocalSpacing
import com.kiptechie.tracker_domain.models.MealType
import com.kiptechie.tracker_presentation.search.components.SearchTextField
import com.kiptechie.tracker_presentation.search.components.TrackableFoodItem
import java.time.LocalDate

@ExperimentalCoilApi
@ExperimentalComposeUiApi
@Composable
fun SearchScreen(
    scaffoldState: ScaffoldState,
    mealName: String,
    dayOfMonth: Int,
    month: Int,
    year: Int,
    onNavigateUp: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val dimens = LocalSpacing.current
    val state = viewModel.state
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(key1 = keyboardController, block = {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    keyboardController?.hide()
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                }
                is UiEvent.NavigateUp -> onNavigateUp()
                else -> Unit
            }
        }
    })
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimens.medium)
    ) {
        Text(
            text = stringResource(id = R.string.add_meal, mealName),
            style = MaterialTheme.typography.h4
        )
        Spacer(modifier = Modifier.height(dimens.medium))
        SearchTextField(
            text = state.query,
            onValueChange = {
                viewModel.onEvent(SearchEvent.OnQueryChange(it))
            },
            shouldShowHint = state.isHintVisible,
            onSearch = {
                keyboardController?.hide()
                viewModel.onEvent(SearchEvent.OnSearch)
            },
            onFocusChanged = {
                viewModel.onEvent(SearchEvent.OnSearchFocusChange(it.isFocused))
            }
        )
        Spacer(modifier = Modifier.height(dimens.medium))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            content = {
                items(state.trackableFood) { trackableFoodUiState ->
                    TrackableFoodItem(
                        modifier = Modifier.fillMaxWidth(),
                        trackableFoodUiState = trackableFoodUiState,
                        onClick = {
                            viewModel.onEvent(SearchEvent.OnToggleTrackableFood(trackableFoodUiState.food))
                        },
                        onAmountChange = {
                            viewModel.onEvent(
                                SearchEvent.OnAmountForFoodChange(
                                    trackableFoodUiState.food,
                                    it
                                )
                            )
                        },
                        onTrack = {
                            keyboardController?.hide()
                            viewModel.onEvent(
                                SearchEvent.OnTrackFoodClick(
                                    food = trackableFoodUiState.food,
                                    mealType = MealType.fromString(mealName),
                                    date = LocalDate.of(year, month, dayOfMonth)
                                )
                            )
                        }
                    )
                }
            }
        )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isSearching -> CircularProgressIndicator()
            state.trackableFood.isEmpty() -> {
                Text(
                    text = stringResource(id = R.string.no_results),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}