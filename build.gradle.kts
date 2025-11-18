plugins {
    kotlin("multiplatform") version "2.0.0" apply false
    id("com.android.application") version "8.5.0" apply false
    id("com.android.library") version "8.5.0" apply false

    // REQUIRED FOR COMPOSE MULTIPLATFORM (KOTLIN 2.0)
    id("org.jetbrains.compose") version "1.6.10" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
