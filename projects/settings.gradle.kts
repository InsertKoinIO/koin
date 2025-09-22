enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupByRegex(".*google.*")
                includeGroupByRegex(".*android.*")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupByRegex(".*google.*")
                includeGroupByRegex(".*android.*")
            }
        }
        // For Ktor EAP
//        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap") {
//            mavenContent {
//                includeGroupAndSubgroups("io.ktor")
//            }
//        }
        mavenCentral()
    }
}

include(
    // Core
    ":core:koin-core-annotations",
    ":core:koin-core",
    ":core:koin-core-coroutines",
    ":core:koin-core-viewmodel",
//    ":core:koin-core-viewmodel-navigation",
    ":core:koin-test",
    ":core:koin-test-coroutines",
    ":core:koin-test-junit4",
    ":core:koin-test-junit5",
    ":core:benchmark",

    // Fu DSL - Experimental
    ":core:koin-fu",
    ":core:koin-fu-viewmodel",

    // Ktor
    ":ktor:koin-ktor",
    ":ktor:koin-logger-slf4j",
    // Android
    ":android:koin-android",
    ":android:koin-android-compat",
    ":android:koin-dagger-bridge",
    ":android:koin-androidx-navigation",
    ":android:koin-androidx-workmanager",
    ":android:koin-android-test",
    ":android:koin-androidx-startup",
    // Compose
    ":compose:koin-compose",
    ":compose:koin-compose-viewmodel",
    ":compose:koin-compose-viewmodel-navigation",
    ":compose:koin-androidx-compose",
    ":compose:koin-androidx-compose-navigation",
    // Plugin
    ":plugins:koin-gradle-plugin",
    // BOM
    ":bom:koin-bom",
)