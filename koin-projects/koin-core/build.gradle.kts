plugins {
    kotlin("jvm")
    id("koin-versions")
}

description = "Koin - simple dependency injection for Kotlin - $name"

dependencies {
    api(kotlin("stdlib"))

    testApi("junit:junit")
    testApi("org.mockito:mockito-inline")

    testApi("org.jetbrains.kotlinx:kotlinx-coroutines-core")
}
