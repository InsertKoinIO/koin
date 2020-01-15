plugins {
    kotlin("jvm")
}

apply<koin.DependencyManagementPlugin>()

dependencies {
    api(project(":examples:multimodule-ktor:common"))

    // Koin
    testApi(project(":koin-test"))

    // Ktor
    testApi("io.ktor:ktor-server-test-host")
}
