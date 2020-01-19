import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

apply(rootDir.resolve("gradle/target-jvm.gradle.kts"))

description = "Koin - simple dependency injection for Kotlin - $name"

val kotlinVersion: String by extra

configure<KotlinMultiplatformExtension> {
    jvm {
        sourceSets {
            named("jvmMain") {
                dependencies {
                    api(project(":koin-core"))

                    api(kotlin("reflect", kotlinVersion))
                }
            }

            named("jvmMain") {
                dependencies {
                    api(project(":koin-test"))
                }
            }
        }
    }
}

apply(from = rootDir.resolve("gradle/checkstyle.gradle.kts"))

// FIXME Reduce the number of errors to 10 (by default in Detekt)
tasks.withType(Detekt::class) {
    config.setFrom(files(rootDir.resolve("gradle/detekt.yml"), projectDir.resolve("detekt.yml")))
}
