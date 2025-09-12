import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
}

val koinVersion: String by project
version = koinVersion

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

//    // Enable context receivers for all targets
//    targets.all {
//        compilations.all {
//            kotlinOptions {
//                freeCompilerArgs += listOf("-Xcontext-receivers")
//            }
//        }
//    }

    js(IR) {
        nodejs()
        browser()
        binaries.executable()
    }

    wasmJs {
        binaries.executable()
        nodejs()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":core:koin-core"))
            api(libs.jb.lifecycleViewmodel)
            api(libs.jb.lifecycleViewmodelSavedState)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.test.junit)
        }
    }
}

val androidCompileSDK : String by project
val androidMinSDK : String by project

android {
    namespace = "org.koin.viewmodel"
    compileSdk = androidCompileSDK.toInt()
    defaultConfig {
        minSdk = androidMinSDK.toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

apply(from = file("../../gradle/publish.gradle.kts"))
