// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.template.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prauga.pvot.coreui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.prauga.template.core.data.ItemRepository
import org.prauga.template.core.model.Item

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: ItemRepository) : ViewModel() {
    private val attempt = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<UiState<List<Item>>> =
        attempt
            .flatMapLatest {
                repository.items
                    .map<List<Item>, UiState<List<Item>>> { UiState.Success(it) }
                    .catch { emit(UiState.Error(it.message.orEmpty(), it)) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.AddItem -> viewModelScope.launch { repository.add(event.title) }
            HomeEvent.Retry -> attempt.value++
        }
    }
}
