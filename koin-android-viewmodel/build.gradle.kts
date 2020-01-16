plugins {
    id("koin-base-android")
}
description = "Koin project - $name"

dependencies {
    api(project(":koin-android-scope"))

    implementation("android.arch.lifecycle:extensions"){
        exclude(module="livedata")
        exclude(module="runtime")
    }

    testImplementation(project(":koin-test"))
    testImplementation("org.mockito:mockito-inline")
}
