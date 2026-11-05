// Each project builds .generated/android/<scenario>, written by scripts/extract-snippets.mjs,
// then starts the app with Robolectric: the real Application, manifest and resources.
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

plugins {
    kotlin("android") apply false
    kotlin("plugin.compose") apply false
    kotlin("plugin.serialization") apply false
    id("com.android.application") apply false
    id("io.insert-koin.compiler.plugin") apply false
}

val versions = file("../../src/data/versions.ts").readText()
fun version(key: String) = Regex("""$key: '([^']+)'""").find(versions)!!.groupValues[1]

subprojects {
    val generated = rootProject.file("../.generated/android/$name")
    val usesAnnotations = name.endsWith("annotations")
    val usesNavigation3 = name.contains("navigation3")
    val usesLibraries = name.contains("libraries")
    val usesNavScope = name.contains("navscope")

    apply(plugin = "com.android.application")
    apply(plugin = "org.jetbrains.kotlin.android")
    if (!name.endsWith("constructor")) apply(plugin = "io.insert-koin.compiler.plugin")
    apply(plugin = "org.jetbrains.kotlin.plugin.compose")
    if (usesNavigation3 || usesNavScope) apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    configure<com.android.build.api.dsl.ApplicationExtension> {
        namespace = "org.koin.sample"
        compileSdk = 36
        defaultConfig {
            applicationId = "org.koin.sample"
            minSdk = version("androidMinSdk").toInt()
            targetSdk = 36
        }
        buildFeatures.compose = true
        if (usesLibraries) {
            // The buildConfigField values that the library recipes read.
            buildFeatures.buildConfig = true
            defaultConfig.buildConfigField("String", "API_URL", "\"https://example.com/\"")
            defaultConfig.buildConfigField("boolean", "ENABLE_ANALYTICS", "false")
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        testOptions.unitTests.isIncludeAndroidResources = true
        sourceSets.getByName("main") {
            manifest.srcFile(generated.resolve("main/AndroidManifest.xml"))
            java.srcDir(generated.resolve("main/kotlin"))
        }
        sourceSets.getByName("test") {
            java.srcDir(generated.resolve("test"))
        }
    }
    configure<KotlinAndroidProjectExtension> {
        jvmToolchain(17)
    }

    dependencies {
        "implementation"(platform("io.insert-koin:koin-bom:${version("koin")}"))
        if (usesAnnotations || usesNavigation3 || (usesLibraries && !name.endsWith("constructor"))) "implementation"("io.insert-koin:koin-annotations")
        if (usesLibraries) {
            // The libraries of the recipes. The Room compiler is left out: the database is never opened.
            "implementation"("com.squareup.retrofit2:retrofit:2.11.0")
            "implementation"("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
            "implementation"("com.squareup.okhttp3:logging-interceptor:4.12.0")
            "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
            "implementation"("androidx.room:room-runtime:2.7.2")
            "implementation"("androidx.datastore:datastore-preferences:1.1.7")
        }
        if (usesNavigation3) {
            // What the Navigation 3 page installs.
            "implementation"("io.insert-koin:koin-compose-navigation3")
            "implementation"("androidx.navigation3:navigation3-ui:${version("navigation3")}")
            "implementation"("androidx.lifecycle:lifecycle-viewmodel-navigation3:${version("lifecycleNavigation3")}")
        }
        if (usesNavScope) {
            // What the KoinNavigationScope recipe uses.
            "implementation"("io.insert-koin:koin-compose-viewmodel-navigation")
            "implementation"("androidx.navigation:navigation-compose:${version("navigationCompose")}")
            "implementation"("androidx.lifecycle:lifecycle-viewmodel-compose:${version("lifecycleCompose")}")
        }
        "testImplementation"("junit:junit:4.13.2")
        "testImplementation"("org.robolectric:robolectric:4.16")
        "testImplementation"("androidx.test:core:1.7.0")

        "implementation"("io.insert-koin:koin-compose-viewmodel")
        // Brought by the Android Studio template the tutorial starts from.
        "implementation"(platform("androidx.compose:compose-bom:2025.11.01"))
        "implementation"("androidx.compose.material3:material3")
        "implementation"("androidx.activity:activity-compose:1.12.0")
        "testImplementation"(platform("androidx.compose:compose-bom:2025.11.01"))
        "testImplementation"("androidx.compose.ui:ui-test-junit4")
        "debugImplementation"("androidx.compose.ui:ui-test-manifest")
    }

    // What check:snippets runs in each project.
    tasks.register("checkScenario") {
        dependsOn("testDebugUnitTest")
    }
}
