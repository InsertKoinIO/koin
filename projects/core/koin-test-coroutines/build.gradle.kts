import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    
    jvm()

    sourceSets {
        commonMain.dependencies {
            api(project(":core:koin-core"))
            api(project(":core:koin-test"))
            api(project(":core:koin-core-coroutines"))
            api(libs.kotlin.test)
        }
        jvmMain.dependencies {
            api(project(":core:koin-test"))
            api(kotlin("reflect"))
        }
        commonTest.dependencies {
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
