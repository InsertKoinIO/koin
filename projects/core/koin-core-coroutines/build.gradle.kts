import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    
    jvm()

    js(IR) {
        nodejs()
        browser()
        binaries.executable()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
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
            api(libs.kotlin.coroutines)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.test.coroutines)
        }
    }
}

tasks.withType<KotlinCompile>().all {
    compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
}

apply(from = file("../../gradle/publish.gradle.kts"))
