// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.template.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prauga.pvot.coreui.PvotEmptyContent
import com.prauga.pvot.coreui.PvotErrorContent
import com.prauga.pvot.coreui.PvotLoadingContent
import com.prauga.pvot.coreui.UiState
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.theme.PvotSpacing
import org.prauga.template.R
import org.prauga.template.core.model.Item

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(state, viewModel::onEvent)
}

@Composable
private fun HomeContent(state: UiState<List<Item>>, onEvent: (HomeEvent) -> Unit) {
    when (state) {
        UiState.Loading -> PvotLoadingContent()
        is UiState.Error ->
            PvotErrorContent(
                title = stringResource(R.string.home_error_title),
                message = stringResource(R.string.home_error_message),
                onRetry = { onEvent(HomeEvent.Retry) },
            )
        is UiState.Success -> {
            val newTitle = stringResource(R.string.home_new_item, state.data.size + 1)
            PvotScreen(Modifier.fillMaxSize()) {
                item {
                    Text(
                        stringResource(R.string.home_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                if (state.data.isEmpty()) {
                    item { PvotEmptyContent(stringResource(R.string.home_empty)) }
                }
                itemsIndexed(state.data, key = { _, item -> item.id }) { index, item ->
                    PvotCard(Modifier.fillMaxWidth().animateItem().pvotReveal(index)) {
                        Text(
                            item.title,
                            modifier = Modifier.padding(PvotSpacing.L),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                item {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(onClick = { onEvent(HomeEvent.AddItem(newTitle)) }) {
                            Text(stringResource(R.string.home_add))
                        }
                    }
                }
            }
        }
    }
}
