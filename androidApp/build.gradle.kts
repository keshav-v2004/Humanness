plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization") version "2.0.0"

    // MUST NOT SPECIFY VERSION HERE
    id("org.jetbrains.compose")
    // Required with Kotlin 2.0 when compose is enabled
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

android {
    namespace = "com.example.josh.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.josh.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {

    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.components.resources)

    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("androidx.camera:camera-core:1.3.3")
    implementation("androidx.camera:camera-camera2:1.3.3")
    implementation("androidx.camera:camera-lifecycle:1.3.3")
    implementation("androidx.camera:camera-view:1.3.3")

    implementation("androidx.media:media:1.7.0")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")

    implementation(project(":shared"))
}
