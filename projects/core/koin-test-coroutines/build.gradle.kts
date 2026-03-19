import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    
    jvm()

    sourceSets {
        commonMain.dependencies {
            api(projects.core.koinCore)
            api(projects.core.koinTest)
            api(projects.core.koinCoreCoroutines)
            api(libs.kotlin.test)
        }
        jvmMain.dependencies {
            api(projects.core.koinTest)
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
