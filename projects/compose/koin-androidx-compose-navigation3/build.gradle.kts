import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

val androidCompileSDK : String by project
val androidComposeMinSDK : String by project

android {
    namespace = "org.koin.androidx.navigation3"
    compileSdk = androidCompileSDK.toInt()
    defaultConfig {
        minSdk = androidComposeMinSDK.toInt()
    }
    buildFeatures {
        buildConfig = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
    publishing {
        singleVariant("release") {}
    }
}

tasks.withType<KotlinCompile>().all {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    api(project(":compose:koin-androidx-compose"))
    implementation(libs.androidx.navigation3.runtime)

    // tests
    testImplementation(project(":core:koin-test"))
    testImplementation(project(":core:koin-test-junit4"))
    testImplementation(libs.kotlin.test)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockito)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.robolectric)
}

// android sources
val sourcesJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.map { it.java.srcDirs })
}

apply(from = file("../../gradle/publish-android.gradle.kts"))
