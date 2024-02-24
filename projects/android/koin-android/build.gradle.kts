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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    api(project(":core:koin-core"))
    api(libs.android.appcompat)
    api(libs.android.activity)
    api(libs.android.fragment)
    api(libs.androidx.viewmodel)
    api(libs.androidx.commonJava8)

    // tests
    testImplementation(project(":core:koin-test"))
    testImplementation(project(":core:koin-test-junit4"))
    testImplementation(libs.kotlin.test)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockito)
    testImplementation(libs.test.mockk)
}

apply(from = file("../../gradle/publish-android.gradle.kts"))
