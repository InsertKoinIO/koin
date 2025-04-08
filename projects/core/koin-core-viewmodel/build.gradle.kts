import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

val koinVersion: String by project
version = koinVersion

kotlin {
    jvmToolchain(1_8)
    jvm {
        withJava()
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
            api(libs.jb.bundle)
            api(libs.jb.savedstate)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.test.junit)
            implementation(libs.test.mockk)
        }
    }
}

tasks.withType<KotlinCompile>().all {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

apply(from = file("../../gradle/publish.gradle.kts"))
