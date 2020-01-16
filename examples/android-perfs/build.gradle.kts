plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}
apply<koin.DependencyManagementPlugin>()

val androidMinVersion: String by extra
val androidTargetVersion: String by extra
val androidBuildToolsVersion: String by extra

android {
    compileSdkVersion(androidTargetVersion.toInt())
    buildToolsVersion = androidBuildToolsVersion

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(androidTargetVersion.toInt())
        applicationId = "org.koin.sample.android"
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":koin-core"))

    implementation("com.android.support:appcompat-v7")
    implementation("android.arch.lifecycle:extensions")

    implementation("org.jetbrains.anko:anko-commons")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    testImplementation("junit:junit")
}
