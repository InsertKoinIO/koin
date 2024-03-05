@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

val androidCompileSDK : String by project
val androidMinSDK : String by project
val jetpackComposeCompiler : String by project

android {
    compileSdk = androidCompileSDK.toInt()
    defaultConfig {
        minSdk = androidMinSDK.toInt()
    }
    buildFeatures {
        buildConfig = false
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = jetpackComposeCompiler
    }
}

dependencies {
    implementation(project(":compose:koin-androidx-compose"))
    implementation(libs.androidx.composeNavigation)
}

apply(from = file("../../gradle/publish-android.gradle.kts"))
