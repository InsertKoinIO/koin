description = "Koin - simple dependency injection for Kotlin - $name"

apply(from = rootDir.resolve("gradle/target-jvm.gradle.kts"))

val slf4jVersion: String by extra

dependencies {
    // Koin
    "api"(project(":koin-core"))

    // SLF4J
    "api"("org.slf4j:slf4j-api:$slf4jVersion")
}
