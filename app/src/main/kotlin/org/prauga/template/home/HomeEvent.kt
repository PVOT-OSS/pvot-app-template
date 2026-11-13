// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.template.home

sealed interface HomeEvent {
    data class AddItem(val title: String) : HomeEvent

    data object Retry : HomeEvent
}
