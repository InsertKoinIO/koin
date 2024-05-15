import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
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

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":compose:koin-compose"))
            api(libs.compose.jb)
            api(libs.androidx.composeViewModel)
        }
    }
}

apply(from = file("../../gradle/publish.gradle.kts"))
