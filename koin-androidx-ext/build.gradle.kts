plugins {
    id("koin-base-android")
}
description = "Koin project - $name"

dependencies {
    api(project(":koin-core-ext"))
    api(project(":koin-androidx-viewmodel"))

    implementation("androidx.lifecycle:lifecycle-extensions") {
        exclude(module = "lifecycle-livedata")
        exclude(module = "lifecycle-service")
        exclude(module = "lifecycle-process")
        exclude(module = "runtime")
        exclude(group = "androidx.legacy")
    }

    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate") {
        exclude(module = "lifecycle-livedata")
        exclude(module = "lifecycle-service")
        exclude(module = "lifecycle-process")
        exclude(module = "runtime")
        exclude(group = "androidx.legacy")
    }

    testImplementation(project(":koin-test"))
    testImplementation("org.mockito:mockito-inline")
}
