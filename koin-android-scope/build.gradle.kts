plugins {
    id("koin-base-android")
}
description = "Koin project - $name"

dependencies {
    implementation("com.android.support:appcompat-v7")
    implementation("android.arch.lifecycle:common")

    api(project(":koin-android"))

    testImplementation(project(":koin-test"))
    testImplementation("org.mockito:mockito-inline")
}
