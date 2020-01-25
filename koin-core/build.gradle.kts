import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

description = "Koin - simple dependency injection for Kotlin - $name"

apply(from = rootDir.resolve("gradle/target-jvm.gradle.kts"))

configure<KotlinMultiplatformExtension> {
    jvm {
        withJava()
    }
}

// FIXME Reduce the number of errors to 10 (by default in Detekt)
tasks.withType(Detekt::class) {
    config.setFrom(files(rootDir.resolve("gradle/detekt.yml"), projectDir.resolve("detekt.yml")))
}
