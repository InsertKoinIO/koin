import org.gradle.kotlin.dsl.android
import org.gradle.kotlin.dsl.api
import org.gradle.kotlin.dsl.jvm
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

val koinVersion: String by project
version = koinVersion

kotlin {
    jvmToolchain(1_8)
    jvm()
    android {
        publishLibraryVariants("release")
    }

    js(IR) {
        nodejs()
        browser()
        binaries.executable()
    }

    wasmJs {
        nodejs()
        binaries.executable()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":core:koin-core"))
            api(libs.jb.composeRuntime)
        }
        androidMain.dependencies {
            api(project(":android:koin-android"))
            api(libs.androidx.composeRuntime)
            api(libs.androidx.composeFoundation)
        }
        nativeMain.dependencies {
        }
        wasmJsMain.dependencies {
        }
        jsMain.dependencies {
        }
    }
}

val androidCompileSDK : String by project
val androidMinSDK : String by project

android {
    namespace = "org.koin.compose"
    compileSdk = androidCompileSDK.toInt()
    defaultConfig {
        minSdk = androidMinSDK.toInt()
    }
    buildFeatures {
        buildConfig = false
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<KotlinCompile>().all {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

apply(from = file("../../gradle/publish.gradle.kts"))
