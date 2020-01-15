plugins {
    id("koin-base-jvm")
}

description = "Koin - simple dependency injection for Kotlin - $name"

dependencies {
    api(project(":koin-core"))

    api(kotlin("reflect"))

    testApi(project(":koin-test"))
}
