import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm {
        withJava()
    }

    js(IR) { // or: LEGACY, BOTH
        // ...
        nodejs()
        browser()
        binaries.executable() // not applicable to BOTH, see details below
    }

    wasmJs {
        binaries.executable()
        nodejs()
    }

    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
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
        jvmMain.dependencies {
            implementation(kotlin("stdlib-jdk8"))
        }
        jvmTest.dependencies {
            implementation(kotlin("test-junit"))
            implementation(libs.test.junit)
            implementation(libs.test.mockito)
        }
        commonMain.dependencies {
            implementation(libs.extras.stately)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.test.coroutines)
        }
    }
}

rootProject.the<NodeJsRootExtension>().apply {
    nodeVersion = "21.0.0-v8-canary202309143a48826a08"
    nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask>().configureEach {
    args.add("--ignore-engines")
}