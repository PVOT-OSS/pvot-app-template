// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.template.core.data

import kotlinx.coroutines.flow.Flow
import org.prauga.template.core.model.Item

interface ItemRepository {
    val items: Flow<List<Item>>

    suspend fun add(title: String)
}
