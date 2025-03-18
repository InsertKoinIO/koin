import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

val androidCompileSDK : String by project
val androidMinSDK : String by project

android {
    namespace = "org.koin.androidx.startup"
    compileSdk = androidCompileSDK.toInt()
    defaultConfig {
        minSdk = androidMinSDK.toInt()
    }
    buildFeatures {
        buildConfig = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<KotlinCompile>().all {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

dependencies {
    api(project(":android:koin-android"))
    api(libs.androidx.startup)

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
