import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose)
}

val koinComposeVersion: String by project
version = koinComposeVersion

kotlin {
    jvm {
        withJava()
    }

    js(IR) {
        browser()
    }

    wasmJs {
        browser()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":core:koin-core"))
            api(libs.compose.jb)
        }
    }
}

apply(from = file("../../gradle/publish.gradle.kts"))
