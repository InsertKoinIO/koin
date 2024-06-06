import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm {
        withJava()
    }

    macosX64()
    macosArm64()
    mingwX64()
    linuxX64()
    linuxArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":core:koin-core"))

            // Ktor
            api(libs.ktor.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)

            // Ktor
            implementation(libs.ktor.testHost)
            implementation(libs.ktor.cio)
        }

        jvmTest.dependencies {
            implementation(libs.ktor.netty)
        }
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

apply(from = file("../../gradle/publish.gradle.kts"))
