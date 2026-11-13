// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.pvot.android.application)
    alias(libs.plugins.pvot.android.compose)
    alias(libs.plugins.pvot.hilt)
}

android {
    namespace = "org.prauga.template"

    defaultConfig {
        applicationId = "org.prauga.template"
        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"] as String
    }

    buildTypes {
        debug { applicationIdSuffix = ".dev" }
        release { optimization { enable = true } }
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(libs.pvot.design.system)
    implementation(libs.pvot.core.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    testImplementation(libs.kotlinx.coroutines.test)
}
