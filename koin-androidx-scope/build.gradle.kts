plugins {
    id("koin-base-android")
}
description = "Koin project - $name"

dependencies {
    api(project(":koin-android"))

    implementation("androidx.appcompat:appcompat")
    implementation("androidx.lifecycle:lifecycle-common") {
        exclude(group = "core-runtime")
    }

    testImplementation(project(":koin-test"))
    testImplementation("org.mockito:mockito-inline")
}
