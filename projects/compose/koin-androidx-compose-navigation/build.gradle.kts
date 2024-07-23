@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

val androidCompileSDK : String by project
val androidMinSDK : String by project

android {
    namespace = "org.koin.androidx.compose.navigation"
    compileSdk = androidCompileSDK.toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    defaultConfig {
        minSdk = androidMinSDK.toInt()
    }
    buildFeatures {
        buildConfig = false
        compose = true
    }
}

dependencies {
    api(project(":compose:koin-androidx-compose"))
    api(libs.androidx.composeNavigation)
}

// android sources
val sourcesJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.map { it.java.srcDirs })
}

apply(from = file("../../gradle/publish-android.gradle.kts"))
