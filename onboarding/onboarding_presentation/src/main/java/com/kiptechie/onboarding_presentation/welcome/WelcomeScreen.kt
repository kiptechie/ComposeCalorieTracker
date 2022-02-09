package com.kiptechie.onboarding_presentation.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.kiptechie.core.R
import com.kiptechie.core.navigation.Route
import com.kiptechie.core.util.UiEvent
import com.kiptechie.core_ui.LocalSpacing
import com.kiptechie.onboarding_presentation.components.ActionButton

@Composable
fun WelcomeScreen(
    onNavigate: (UiEvent.Navigate) -> Unit
) {
    val dimens = LocalSpacing.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimens.medium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.welcome_text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h2,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(dimens.medium))
        ActionButton(
            text = stringResource(id = R.string.next),
            onClick = {
                onNavigate(UiEvent.Navigate(Route.GENDER))
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}