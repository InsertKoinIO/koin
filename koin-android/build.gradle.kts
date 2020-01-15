plugins {
    id("koin-base-android")
}

description = "Koin project - $name"

dependencies {
    api(project(":koin-core"))

    testImplementation(project(":koin-test"))
    testImplementation("org.mockito:mockito-inline")
}
