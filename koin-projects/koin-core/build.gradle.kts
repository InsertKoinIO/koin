plugins {
    id("koin-base-jvm")
}

description = "Koin - simple dependency injection for Kotlin - $name"

dependencies {

    testApi("junit:junit")
    testApi("org.mockito:mockito-inline")

    testApi("org.jetbrains.kotlinx:kotlinx-coroutines-core")
}
