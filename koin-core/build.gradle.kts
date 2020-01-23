import io.gitlab.arturbosch.detekt.Detekt

description = "Koin - simple dependency injection for Kotlin - $name"

apply(from = rootDir.resolve("gradle/target-jvm.gradle.kts"))
// FIXME Reduce the number of errors to 10 (by default in Detekt)
tasks.withType(Detekt::class) {
    config.setFrom(files(rootDir.resolve("gradle/detekt.yml"), projectDir.resolve("detekt.yml")))
}
