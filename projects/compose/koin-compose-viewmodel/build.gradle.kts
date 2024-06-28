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

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":core:koin-core"))
            api(project(":compose:koin-compose"))

            api(libs.compose.jb)
            api(libs.kotlin.coroutines)

        }
    }
}

apply(from = file("../../gradle/publish.gradle.kts"))
