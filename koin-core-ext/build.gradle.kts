description = "Koin - simple dependency injection for Kotlin - $name"

apply(from = rootDir.resolve("gradle/target-jvm.gradle.kts"))

val kotlinVersion: String by extra

dependencies {
    // Koin
    "api"(project(":koin-core"))

    "api"(kotlin("reflect", kotlinVersion))

    "testApi"(project(":koin-test"))
}