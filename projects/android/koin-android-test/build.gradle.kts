plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

val androidCompileSDK : String by project
val androidMinSDK : String by project

android {
    namespace = "org.koin.android.test"
    compileSdk = androidCompileSDK.toInt()
    defaultConfig {
        minSdk = androidMinSDK.toInt()
    }
    buildFeatures {
        buildConfig = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    api(project(":android:koin-android"))
    api(project(":android:koin-androidx-workmanager"))
    api(project(":core:koin-test"))
    // Test
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockito)
}

// android sources
val sourcesJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.map { it.java.srcDirs })
}

apply(from = file("../../gradle/publish-android.gradle.kts"))
