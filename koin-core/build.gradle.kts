import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

apply(from = rootProject.file("gradle/target-jvm.gradle.kts"))

description = "Koin - simple dependency injection for Kotlin - $name"

configure<KotlinMultiplatformExtension> {
    jvm {
        withJava()
    }
}

apply(from = rootProject.file("gradle/checkstyle.gradle.kts"))
// FIXME Reduce the number of errors to 10 (by default in Detekt)
tasks.withType(Detekt::class) {
    config.setFrom(files(rootDir.resolve("gradle/detekt.yml"), projectDir.resolve("detekt.yml")))
}
