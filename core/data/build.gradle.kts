// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.pvot.android.library)
    alias(libs.plugins.pvot.hilt)
}

android { namespace = "org.prauga.template.core.data" }

dependencies {
    api(project(":core:model"))
    api(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}
