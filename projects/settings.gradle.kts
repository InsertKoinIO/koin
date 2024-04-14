enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(
    // Core
    ":core:koin-core",
    ":core:koin-core-coroutines",
    ":core:koin-test",
    ":core:koin-test-junit4",
    ":core:koin-test-junit5",
    // Ktor
    ":ktor:koin-ktor",
    ":ktor:koin-logger-slf4j",
    // Android
    ":android:koin-android",
    ":android:koin-android-compat",
    ":android:koin-androidx-navigation",
    ":android:koin-androidx-workmanager",
    ":android:koin-android-test",
    // Compose
    ":compose:koin-compose",
    ":compose:koin-androidx-compose",
    ":compose:koin-androidx-compose-navigation",
    ":compose:koin-decompose",
    // Plugin
    ":plugins:koin-gradle-plugin",
    // BOM
    ":bom:koin-bom",
)