plugins {
    id("koin-base-android")
}
description = "Koin project - $name"


dependencies {
    api(project(":koin-android"))

    implementation("androidx.fragment:fragment")

    testImplementation(project(":koin-test"))
    testImplementation("org.mockito:mockito-inline")
}
