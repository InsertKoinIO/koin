description = "Koin - simple dependency injection for Kotlin - $name"

apply(from = rootDir.resolve("gradle/target-jvm.gradle.kts"))

val slf4jVersion: String by extra

configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
    jvm {
        sourceSets {
            named("jvmMain") {
                dependencies {
                    // Koin
                    api(project(":koin-core"))

                    // SLF4J
                    api("org.slf4j:slf4j-api:$slf4jVersion")
                }
            }
        }
    }
}