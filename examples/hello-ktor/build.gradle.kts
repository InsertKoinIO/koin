import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
    kotlin("jvm")
}
apply<koin.DependencyManagementPlugin>()

dependencies {
    api(kotlin("stdlib-jdk8"))

    api(project(":koin-ktor"))

    testApi(project(":koin-test"))

    // Ktor
    api("io.ktor:ktor-server-netty")
    testApi("io.ktor:ktor-server-test-host")

    // Logging
    api(project(":koin-logger-slf4j"))
    api("ch.qos.logback:logback-classic")
}

kotlin {
    experimental {
        coroutines = Coroutines.ENABLE
    }
}
