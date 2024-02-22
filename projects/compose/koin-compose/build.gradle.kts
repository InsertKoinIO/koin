import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose)
}

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
            implementation(libs.compose.jb)
        }
    }
}

apply(from = file("../../gradle/publish.gradle.kts"))
