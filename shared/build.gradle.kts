plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization") version "2.0.0"

    // Optional: shared UI via Compose MPP
    id("org.jetbrains.compose")
    // Required with Kotlin 2.0 for Compose Multiplatform
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

kotlin {
    androidTarget()
    // Removed unused desktop target to align with available actuals

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
                implementation("com.russhwolf:multiplatform-settings:1.1.1")

                // Compose MPP optional
                implementation(compose.runtime)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core-ktx:1.13.0")
            }
        }
    }
}

android {
    namespace = "com.sampletask.shared"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
