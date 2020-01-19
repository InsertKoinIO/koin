import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

apply(rootDir.resolve("gradle/target-jvm.gradle.kts"))

description = "Koin - simple dependency injection for Kotlin - $name"

val junitVersion: String by extra

configure<KotlinMultiplatformExtension> {
    jvm {
        sourceSets {
            named("jvmMain") {
                dependencies {
                    api(project(":koin-core"))
                    api("junit:junit:$junitVersion")
                }
            }
        }
    }
}

apply(from = rootDir.resolve("gradle/checkstyle.gradle.kts"))
