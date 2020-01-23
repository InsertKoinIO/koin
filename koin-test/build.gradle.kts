description = "Koin - simple dependency injection for Kotlin - $name"

apply(from = rootDir.resolve("gradle/target-jvm.gradle.kts"))

val junitVersion: String by extra
val mockitoVersion: String by extra

dependencies {
    "api"(project(":koin-core"))

    "api"("junit:junit:$junitVersion")
    "testImplementation"("org.mockito:mockito-inline:$mockitoVersion")
}