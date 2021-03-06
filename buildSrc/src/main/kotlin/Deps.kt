/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

object Gradle {
    const val gradleVersion = "6.8.3"
}

const val kotlinVersion = "1.4.30"
const val benManesVersionsVersion = "0.38.0"
const val composeVersion = "1.0.0-beta01"
const val hiltVersion = "2.33-beta"
const val roomVersion = "2.2.5"

object Deps {
    object GradlePlugins {
        const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-alpha08"
        const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
        const val hiltGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$hiltVersion"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.3.2"
        const val appCompat = "androidx.appcompat:appcompat:1.2.0"

        const val composeUi = "androidx.compose.ui:ui:$composeVersion"
        const val composeUiTooling = "androidx.compose.ui:ui-tooling:$composeVersion"
        const val composeFoundation = "androidx.compose.foundation:foundation:$composeVersion"
        const val composeMaterial = "androidx.compose.material:material:$composeVersion"
        const val composeMaterialIconsExtended =
            "androidx.compose.material:material-icons-extended:$composeVersion"
        const val composeActivity = "androidx.activity:activity-compose:1.3.0-alpha03"
        const val composeViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha02"
        const val composeLiveData = "androidx.compose.runtime:runtime-livedata:$composeVersion"

        const val room = "androidx.room:room-runtime:$roomVersion"
        const val roomCompiler = "androidx.room:room-compiler:$roomVersion"

        const val extJunit = "androidx.test.ext:junit:1.1.2"
        const val espresso = "androidx.test.espresso:espresso-core:3.3.0"

    }

    const val ktlint = "com.pinterest:ktlint:0.40.0"

    const val hilt = "com.google.dagger:hilt-android:$hiltVersion"
    const val hiltCompiler = "com.google.dagger:hilt-compiler:$hiltVersion"

    const val material = "com.google.android.material:material:1.3.0"
    const val jUnit = "junit:junit:4.13.2"
}

val PluginDependenciesSpec.daggerHilt: PluginDependencySpec
    get() = id("dagger.hilt.android.plugin")

val PluginDependenciesSpec.benManesVersions: PluginDependencySpec
    get() = id("com.github.ben-manes.versions") version benManesVersionsVersion

val PluginDependenciesSpec.ktlint: PluginDependencySpec
    get() = id("com.cmgapps.gradle.ktlint")

