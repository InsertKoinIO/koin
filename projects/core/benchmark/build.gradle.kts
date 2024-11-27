import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    kotlin("plugin.allopen") version "2.0.20"
    alias(libs.plugins.benchmark)
}

kotlin {
    jvmToolchain(1_8)
    jvm {
        withJava()
    }
    macosX64()
    macosArm64()


    sourceSets {
        commonMain.dependencies {
            implementation(libs.benchmark.runtime)
            api(project(":core:koin-core"))
        }
        jvmMain.dependencies {

        }
        nativeMain.dependencies {
            
        }
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    targets {
        register("jvm")
        register("macosX64")
        register("macosArm64")
    }
}
