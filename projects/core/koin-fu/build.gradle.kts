import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    
    jvm()

    sourceSets {
        jvmMain.dependencies {
            api(kotlin("reflect"))
            api(project(":core:koin-core"))
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

tasks.withType<KotlinCompile>().all {
    compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
}

apply(from = file("../../gradle/publish.gradle.kts"))
