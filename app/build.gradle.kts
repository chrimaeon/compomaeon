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

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    daggerHilt
    ktlint
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.cmgapps.android.compomaeon"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf(
                        "room.schemaLocation" to "$projectDir/schemas",
                        "room.incremental" to "true",
                        "room.expandProjection" to "true"
                    )
                )
            }
        }
    }

    buildFeatures {
        // Enables Jetpack Compose for this module
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta01"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

dependencies {
    implementation(Deps.AndroidX.coreKtx)
    implementation(Deps.AndroidX.appCompat)
    implementation(Deps.material)

    implementation(Deps.AndroidX.lifecycleLivedataKtx)

    implementation(Deps.AndroidX.composeUi)
    // Tooling support (Previews, etc.)
    implementation(Deps.AndroidX.composeUiTooling)
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation(Deps.AndroidX.composeFoundation)
    // Material Design
    implementation(Deps.AndroidX.composeMaterial)
    // Material design icons
    implementation(Deps.AndroidX.composeMaterialIconsExtended)
    // Integration with activities
    implementation(Deps.AndroidX.composeActivity)
    // Integration with ViewModels
    implementation(Deps.AndroidX.composeViewModel)
    // Integration with observables
    implementation(Deps.AndroidX.composeLiveData)

    implementation(Deps.hilt)
    kapt(Deps.hiltCompiler)

    implementation(Deps.AndroidX.room)
    kapt(Deps.AndroidX.roomCompiler)

    testImplementation(Deps.jUnit)

    androidTestImplementation(Deps.AndroidX.extJunit)
    androidTestImplementation(Deps.AndroidX.espresso)
}
