import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

description = "Koin - simple dependency injection for Kotlin - $name"

apply(from = rootDir.resolve("gradle/target-jvm.gradle.kts"))

val kotlinVersion: String by extra

configure<KotlinMultiplatformExtension> {
    jvm {
        sourceSets {
            named("jvmMain") {
                dependencies {
                    // Koin
                    api(project(":koin-core"))

                    api(kotlin("reflect", kotlinVersion))
                }
            }
        }

        sourceSets {
            named("jvmTest") {
                dependencies {
                    api(project(":koin-test"))
                }
            }
        }
    }
}
