plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api(projects.core.koinCoreAnnotations)
        api(projects.core.koinAnnotations)
        api(projects.core.koinCore)
        api("io.insert-koin:koin-core-jvm:$version") //Check later KMP Bom
        api(projects.core.koinCoreCoroutines)
        api(projects.core.koinCoreViewmodel)
        api(projects.core.koinTest)
        api(projects.core.koinTestCoroutines)
        api(projects.core.koinTestJunit4)
        api(projects.core.koinTestJunit5)

        api(projects.ktor.koinKtor)
        api("io.insert-koin:koin-ktor-jvm:$version") //Check later KMP Bom
        api(projects.ktor.koinLoggerSlf4j)

        api(projects.android.koinAndroid)
        api(projects.android.koinAndroidCompat)
        api(projects.android.koinAndroidTest)
        api(projects.android.koinAndroidxNavigation)
        api(projects.android.koinAndroidxWorkmanager)
        api(projects.android.koinAndroidxStartup)
        api(projects.android.koinDaggerBridge)

        api(projects.compose.koinCompose)
        api(projects.compose.koinComposeNavigation3)
        api(projects.compose.koinComposeViewmodel)
        api(projects.compose.koinComposeViewmodelNavigation)
        api(projects.compose.koinAndroidxCompose)
        api(projects.compose.koinAndroidxComposeNavigation)
    }
}

apply(from = file("../../gradle/publish-pom.gradle.kts"))
