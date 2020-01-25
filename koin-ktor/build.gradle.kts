import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

description = "Koin - simple dependency injection for Ktor"

apply(from = rootDir.resolve("gradle/target-jvm.gradle.kts"))

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    maven(url = "https://dl.bintray.com/kotlin/ktor")
}

val ktorVersion: String by extra

configure<KotlinMultiplatformExtension> {
    jvm {
        sourceSets {
            named("jvmMain") {
                dependencies {
                    api(project(":koin-core-ext"))

                    // Ktor
                    api("io.ktor:ktor-server-core:$ktorVersion")
                }
            }

            named("jvmTest") {
                dependencies {
                    implementation(project(":koin-test"))

                    // Ktor
                    api("io.ktor:ktor-server-test-host:$ktorVersion")
                }
            }
        }
    }
}