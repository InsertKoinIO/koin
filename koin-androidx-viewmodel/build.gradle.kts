plugins {
    id("koin-base-android")
}
description = "Koin project - $name"

dependencies {
    api(project(":koin-androidx-scope"))

    // Architecture ViewModel
    implementation("androidx.lifecycle:lifecycle-extensions"){
        exclude(module="lifecycle-livedata")
        exclude(module="lifecycle-service")
        exclude(module="lifecycle-process")
        exclude(module="runtime")
        exclude(group="androidx.legacy")
    }

    testImplementation(project(":koin-test"))
    testImplementation("org.mockito:mockito-inline")
}
