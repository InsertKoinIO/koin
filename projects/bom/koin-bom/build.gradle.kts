import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api(project(":core:koin-core"))
        api(project(":core:koin-core-coroutines"))
        api(project(":core:koin-test"))
        api(project(":core:koin-test-junit4"))
        api(project(":core:koin-test-junit5"))

        api(project(":ktor:koin-ktor"))
        api(project(":ktor:koin-logger-slf4j"))

        api(project(":android:koin-android"))
        api(project(":android:koin-android-compat"))
        api(project(":android:koin-android-test"))
        api(project(":android:koin-androidx-navigation"))
        api(project(":android:koin-androidx-workmanager"))

        api(project(":compose:koin-compose"))
        api(project(":compose:koin-androidx-compose"))
        api(project(":compose:koin-androidx-compose-navigation"))
    }
}

apply(from = file("../../gradle/publish-pom.gradle.kts"))
