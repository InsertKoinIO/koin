plugins {
    id("koin-base-jvm")
}

description = "Koin - simple dependency injection for Kotlin - $name"

dependencies {
    api(project(":koin-core"))

    api("org.slf4j:slf4j-api")
}
