plugins {
    id("koin-base-jvm")
}

description = "Koin - simple dependency injection for Ktor"

dependencies {
    api(project(":koin-core-ext"))
    api("io.ktor:ktor-server-core")

    testApi(project(":koin-test"))
    testApi("io.ktor:ktor-server-test-host")
}
