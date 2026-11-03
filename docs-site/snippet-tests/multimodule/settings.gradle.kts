// A real multi-module Android app, once per style: this is what proves that visibility and
// discovery work across Gradle modules. Versions come from the versions file of the site.
pluginManagement {
    val versions = file("../../src/data/versions.ts").readText()
    fun version(key: String) = Regex("""$key: '([^']+)'""").find(versions)!!.groupValues[1]

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("android") version version("kotlinMin")
        id("com.android.application") version "8.13.2"
        id("com.android.library") version "8.13.2"
        id("io.insert-koin.compiler.plugin") version version("compilerPlugin")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "snippet-tests-multimodule"
for (style in listOf("dsl", "annotations")) {
    for (module in listOf("app", "feature-login", "feature-profile", "core-data", "core-domain")) {
        include(":$style:$module")
    }
}
