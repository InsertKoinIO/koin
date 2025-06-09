plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    sourceSets {
        jvmMain.dependencies {
            api(project(":core:koin-fu"))
            api(project(":core:koin-core-viewmodel"))
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

val androidCompileSDK: String by project

android {
    namespace = "org.koin.dsl.fu"
    compileSdk = androidCompileSDK.toInt()
}

apply(from = file("../../gradle/publish.gradle.kts"))
