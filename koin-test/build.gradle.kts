plugins {
    id("koin-base-jvm")
}

description = "Koin - simple dependency injection for Kotlin - $name"

dependencies {
    api(project(":koin-core"))

    api("junit:junit")
    testImplementation("org.mockito:mockito-inline")
}
