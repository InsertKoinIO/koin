plugins {
    `version-catalog`
}

catalog {
    versionCatalog {
        val version = project.version.toString()
        val group = project.group.toString()

        val versionAlias = version("koin", version)

        library("annotations", group, "koin-annotations").versionRef(versionAlias)
        library("core", group, "koin-core").versionRef(versionAlias)
        library("core-annotations", group, "koin-core-annotations").versionRef(versionAlias)
        library("core-coroutines", group, "koin-core-coroutines").versionRef(versionAlias)
        library("core-viewmodel", group, "koin-core-viewmodel").versionRef(versionAlias)
        library("test", group, "koin-test").versionRef(versionAlias)
        library("test-coroutines", group, "koin-test-coroutines").versionRef(versionAlias)
        library("test-junit4", group, "koin-test-junit4").versionRef(versionAlias)
        library("test-junit5", group, "koin-test-junit5").versionRef(versionAlias)

        library("ktor", group, "koin-ktor").versionRef(versionAlias)
        library("logger-slf4j", group, "koin-logger-slf4j").versionRef(versionAlias)

        library("android", group, "koin-android").versionRef(versionAlias)
        library("android-compat", group, "koin-android-compat").versionRef(versionAlias)
        library("android-test", group, "koin-android-test").versionRef(versionAlias)
        library("androidx-navigation", group, "koin-androidx-navigation").versionRef(versionAlias)
        library("androidx-startup", group, "koin-androidx-startup").versionRef(versionAlias)
        library("androidx-workmanager", group, "koin-androidx-workmanager").versionRef(versionAlias)
        library("dagger-bridge", group, "koin-dagger-bridge").versionRef(versionAlias)

        library("androidx-compose", group, "koin-androidx-compose").versionRef(versionAlias)
        library("androidx-compose-navigation", group, "koin-androidx-compose-navigation").versionRef(versionAlias)
        library("compose", group, "koin-compose").versionRef(versionAlias)
        library("compose-navigation3", group, "koin-compose-navigation3").versionRef(versionAlias)
        library("compose-viewmodel", group, "koin-compose-viewmodel").versionRef(versionAlias)
        library("compose-viewmodel-navigation", group, "koin-compose-viewmodel-navigation").versionRef(versionAlias)
    }
}

apply(from = file("../../gradle/publish-catalog.gradle.kts"))