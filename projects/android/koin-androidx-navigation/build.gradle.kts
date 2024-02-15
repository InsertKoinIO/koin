plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

val androidCompileSDK : String by project
val androidMinSDK : String by project

android {
    compileSdk = androidCompileSDK.toInt()
    defaultConfig {
        minSdk = androidMinSDK.toInt()
    }
    buildFeatures {
        buildConfig = false
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(project(":android:koin-android"))
    api(libs.androidx.navigation)
}

apply(from = file("../../gradle/publish-android.gradle.kts"))
