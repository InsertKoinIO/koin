plugins {
    kotlin("jvm")
}

apply<koin.DependencyManagementPlugin>()

dependencies {
    api(project(":examples:multimodule-ktor:module-a"))
    api(project(":examples:multimodule-ktor:module-b"))

    api(project(":koin-logger-slf4j"))

    api("io.ktor:ktor-server-netty")

    testApi(project(":koin-test"))
    testApi("io.ktor:ktor-server-test-host")
}
