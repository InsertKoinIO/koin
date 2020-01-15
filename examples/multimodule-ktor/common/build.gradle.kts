plugins {
    kotlin("jvm")
}

apply<koin.DependencyManagementPlugin>()

dependencies {
    // Kotlin
    api(kotlin("stdlib-jdk8"))

    // Koin
    api(project(":koin-ktor"))
    testApi(project(":koin-test"))

    // Ktor
    testApi("io.ktor:ktor-server-test-host")
}
