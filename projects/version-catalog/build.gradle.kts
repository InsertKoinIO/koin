plugins {
    `version-catalog`
}

val koinVersion: String by project

catalog {
    versionCatalog {
        // Core
        library("core", "io.insert-koin:koin-core:$koinVersion")
        library("core-coroutines", "io.insert-koin:koin-core-coroutines:$koinVersion")
        library("core-viewmodel", "io.insert-koin:koin-core-viewmodel:$koinVersion")
        library("test", "io.insert-koin:koin-test:$koinVersion")
        library("test-coroutines", "io.insert-koin:koin-test-coroutines:$koinVersion")
        library("test-junit4", "io.insert-koin:koin-test-junit4:$koinVersion")
        library("test-junit5", "io.insert-koin:koin-test-junit5:$koinVersion")
        library("benchmark", "io.insert-koin:koin-benchmark:$koinVersion")
        library("annotations", "io.insert-koin:koin-annotations:$koinVersion")
        // Ktor
        library("ktor", "io.insert-koin:koin-ktor:$koinVersion")
        library("logger-slf4j", "io.insert-koin:koin-logger-slf4j:$koinVersion")
        // Android
        library("android", "io.insert-koin:koin-android:$koinVersion")
        library("android-compat", "io.insert-koin:koin-android-compat:$koinVersion")
        library("androidx-navigation", "io.insert-koin:koin-androidx-navigation:$koinVersion")
        library("androidx-workmanager", "io.insert-koin:koin-androidx-workmanager:$koinVersion")
        library("android-test", "io.insert-koin:koin-android-test:$koinVersion")
        library("androidx-startup", "io.insert-koin:koin-androidx-startup:$koinVersion")
        // Compose
        library("compose", "io.insert-koin:koin-compose:$koinVersion")
        library("compose-viewmodel", "io.insert-koin:koin-compose-viewmodel:$koinVersion")
        library(
            "compose-viewmodel-navigation",
            "io.insert-koin:koin-compose-viewmodel-navigation:$koinVersion"
        )
        library("androidx-compose", "io.insert-koin:koin-androidx-compose:$koinVersion")
        library(
            "androidx-compose-navigation",
            "io.insert-koin:koin-androidx-compose-navigation:$koinVersion"
        )

        plugin("compiler", "io.insert-koin.compiler.plugin").version("1.0.0")

    }
}
apply(from = file("../gradle/publish-catalog.gradle.kts"))
