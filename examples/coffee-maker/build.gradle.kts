plugins {
    kotlin("jvm")
}
apply<koin.DependencyManagementPlugin>()

dependencies {
    api(project(":koin-core"))
    api("org.mockito:mockito-inline")

    testApi(project(":koin-test"))
}
