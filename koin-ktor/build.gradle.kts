description = "Koin - simple dependency injection for Ktor"

apply(from = rootDir.resolve("gradle/target-jvm.gradle.kts"))

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    maven(url = "https://dl.bintray.com/kotlin/ktor")
}

val ktorVersion: String by extra

dependencies {
    "api"(project(":koin-core-ext"))
    "testImplementation"(project(":koin-test"))

    // Ktor
    "api"("io.ktor:ktor-server-core:$ktorVersion")
    "testApi"("io.ktor:ktor-server-test-host:$ktorVersion")
}
