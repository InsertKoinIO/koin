plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api(project(":core:koin-core-annotations"))
        api(project(":core:koin-core"))
        api("io.insert-koin:koin-core-jvm:$version") //Check later KMP Bom
        api(project(":core:koin-core-coroutines"))
        api(project(":core:koin-core-viewmodel"))
        api(project(":core:koin-test"))
        api(project(":core:koin-test-coroutines"))
        api(project(":core:koin-test-junit4"))
        api(project(":core:koin-test-junit5"))

        api(project(":ktor:koin-ktor"))
        api("io.insert-koin:koin-ktor-jvm:$version") //Check later KMP Bom
        api(project(":ktor:koin-logger-slf4j"))

        api(project(":android:koin-android"))
        api(project(":android:koin-android-compat"))
        api(project(":android:koin-android-test"))
        api(project(":android:koin-androidx-navigation"))
        api(project(":android:koin-androidx-workmanager"))
        api(project(":android:koin-androidx-startup"))
        api(project(":android:koin-dagger-bridge"))

        api(project(":compose:koin-compose"))
        api(project(":compose:koin-compose-viewmodel"))
        api(project(":compose:koin-compose-viewmodel-navigation"))
        api(project(":compose:koin-androidx-compose"))
        api(project(":compose:koin-androidx-compose-navigation"))
    }
}

apply(from = file("../../gradle/publish-pom.gradle.kts"))
