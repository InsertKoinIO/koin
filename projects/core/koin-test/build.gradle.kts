import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm {
        withJava()
    }

    js(IR) {
        nodejs()
        browser()
        binaries.executable()

        // To run tests
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += listOf("-XXLanguage:+JsAllowInvalidCharsIdentifiersEscaping")
            }
        }
    }

    wasmJs {
        nodejs()
        browser()
        binaries.executable()

        // To run tests
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += listOf("-XXLanguage:+JsAllowInvalidCharsIdentifiersEscaping")
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    watchosX64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    mingwX64()
    linuxX64()
    linuxArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":core:koin-core"))
            //TODO remove in 3.6
            api(libs.kotlin.test)
        }
        jvmMain.dependencies {
            api(kotlin("reflect"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

rootProject.the<NodeJsRootExtension>().apply {
    nodeVersion = "21.0.0-v8-canary202309143a48826a08"
    nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
}

apply(from = file("../../gradle/publish.gradle.kts"))
