plugins {
    id("koin-base-android")
}
description = "Koin project - $name"

dependencies {
    api(project(":koin-core-ext"))
    api(project(":koin-android-viewmodel"))

    implementation("android.arch.lifecycle:extensions"){
        exclude(module="livedata")
        exclude(module="runtime")
    }

    testImplementation(project(":koin-test"))
    testImplementation("org.mockito:mockito-inline")
}
