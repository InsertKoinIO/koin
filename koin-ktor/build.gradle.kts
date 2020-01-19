import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

apply(rootDir.resolve("gradle/target-jvm.gradle.kts"))

description = "Koin - simple dependency injection for Ktor"

val ktorVersion : String by extra

configure<KotlinMultiplatformExtension> {
    jvm {
        sourceSets {
            named("jvmMain") {
                dependencies {
                    api(project(":koin-core-ext"))
                    api("io.ktor:ktor-server-core:$ktorVersion")
                }
            }

            named("jvmTest") {
                dependencies {
                    api(project(":koin-test"))
                    api("io.ktor:ktor-server-test-host:$ktorVersion")
                }
            }
        }
    }
}

apply(from = rootDir.resolve("gradle/checkstyle.gradle.kts"))
