<!--
SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
SPDX-License-Identifier: Apache-2.0
-->

# PVOT app template

The starting point for every new PVOT-OSS Android app: Kotlin, Jetpack Compose on
[PvotLib](https://github.com/PVOT-OSS/PvotLib), Hilt, and the module, package and build layout of the
PVOT-OSS coding standard.

## What's inside

| path | what |
|---|---|
| `app/` | `:app` — `Application`, `MainActivity`, `AppScaffold`, one feature package per screen (`home/`) |
| `core/model/` | `:core:model` — pure Kotlin models, no Android |
| `core/data/` | `:core:data` — repositories and their Hilt bindings |
| `build-logic/convention/` | `pvot.android.application`, `pvot.android.library`, `pvot.android.compose`, `pvot.hilt`, `pvot.jvm.library` |
| `gradle/libs.versions.toml` | every version, in one place |
| `fastlane/`, `PrivacyPolicy.md` | store listing and privacy policy, kept true to the code |
| `REUSE.toml`, `LICENSES/` | REUSE 3.3 licensing |
| `.github/workflows/` | build, DCO, pre-commit and REUSE checks from `PVOT-OSS/workflows` |

The `home` feature shows the state pattern every screen follows: the ViewModel exposes one
`StateFlow<UiState<…>>` and takes events through `onEvent`; the screen draws loading, error, empty
and content states with PvotLib components.

## Start a new app

```sh
tools/new-app.py --name Notes --package org.prauga.notes --repo PvotNotes --dest ~/apps/Notes
```

This copies the template, renames the package, application ID, app name, theme and `Application`
class, and points the REUSE, privacy policy and store metadata at the new repo. Then replace the
`home` feature with the app's first screen, write the store descriptions and privacy policy for what
the app really does, and set `pvot.minSdk` in `gradle.properties` if it needs more than 27.

A small app can drop `:core:model` and `:core:data`; a large one adds `:feature:<name>` modules.

## Keep an app in step

```sh
tools/drift.py ~/apps/Notes
```

Lists the shared files (build logic, wrapper, CI, pre-commit, editorconfig) and toolchain versions
where the app differs from the template, so a toolchain bump made here can be copied across
deliberately.

## Build

```sh
./gradlew assembleDebug assembleRelease testDebugUnitTest lintDebug
```

JDK 21 and the Android SDK are required. To build against a local PvotLib checkout, set
`pvotLibDir=/path/to/PvotLib` in `~/.gradle/gradle.properties`.
