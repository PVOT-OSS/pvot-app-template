// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.template.core.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.prauga.template.core.model.Item

class InMemoryItemRepositoryTest {
    @Test
    fun addAssignsIncreasingIdsAndTrimsTitles() = runTest {
        val repository = InMemoryItemRepository()

        repository.add(" First ")
        repository.add("Second")

        assertEquals(listOf(Item(1, "First"), Item(2, "Second")), repository.items.first())
    }

    @Test(expected = IllegalArgumentException::class)
    fun addRejectsBlankTitles() = runTest { InMemoryItemRepository().add("  ") }
}
