plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()
    jvm("desktop") // optional

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)

                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")

                // Settings (local storage)
                implementation("com.russhwolf:multiplatform-settings:1.1.1")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core-ktx:1.13.0")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")

                // Activity for Compose
                implementation("androidx.activity:activity-compose:1.9.0")

                // Permissions
                implementation("com.google.accompanist:accompanist-permissions:0.32.0")

                // CameraX
                val cameraxVersion = "1.3.3"
                implementation("androidx.camera:camera-core:$cameraxVersion")
                implementation("androidx.camera:camera-camera2:$cameraxVersion")
                implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
                implementation("androidx.camera:camera-view:1.3.3")

                // Audio Recording (MediaRecorder)
                implementation("androidx.media:media:1.7.0")
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
}
