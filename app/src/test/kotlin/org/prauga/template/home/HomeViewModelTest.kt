// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.template.home

import com.prauga.pvot.coreui.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.prauga.template.core.data.ItemRepository
import org.prauga.template.core.model.Item

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addItemShowsItInTheList() = runTest {
        val viewModel = HomeViewModel(FakeItemRepository())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }

        viewModel.onEvent(HomeEvent.AddItem("First"))

        assertEquals(UiState.Success(listOf(Item(1, "First"))), viewModel.state.value)
    }

    @Test
    fun failureShowsAnError() = runTest {
        val viewModel = HomeViewModel(FailingItemRepository())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect {}
        }

        assertTrue(viewModel.state.value is UiState.Error)
    }

    private class FakeItemRepository : ItemRepository {
        private val state = MutableStateFlow<List<Item>>(emptyList())
        override val items: Flow<List<Item>> = state

        override suspend fun add(title: String) {
            state.update { it + Item(it.size + 1L, title) }
        }
    }

    private class FailingItemRepository : ItemRepository {
        override val items: Flow<List<Item>> = flow { error("disk unavailable") }

        override suspend fun add(title: String) = Unit
    }
}
