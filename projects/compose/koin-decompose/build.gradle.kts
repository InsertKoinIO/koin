@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose)
}

kotlin {
    androidTarget()
    jvm()

    js(IR) {
        browser()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()

    sourceSets {
        androidMain.dependencies {
            api(project(":android:koin-android"))
            api(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            api(project(":core:koin-core"))
            api(project(":compose:koin-compose"))
            implementation(libs.compose.jb)

            api(libs.decompose)
            api(libs.decompose.extensions)
            implementation(compose.foundation)
        }
    }
}

val androidCompileSDK : String by project
val androidMinSDK : String by project
val jetpackComposeCompiler : String by project

android {
    namespace = "org.koin.decompose"
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

apply(from = file("../../gradle/publish.gradle.kts"))


