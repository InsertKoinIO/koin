// Android scenarios, run with Robolectric so that they need the Android SDK but no device.
// Versions come from the single versions file of the site, see ../README.md.
pluginManagement {
    val versions = file("../../src/data/versions.ts").readText()
    fun version(key: String) = Regex("""$key: '([^']+)'""").find(versions)!!.groupValues[1]
    val kotlinVersion = version("kotlinMin")

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("android") version kotlinVersion
        kotlin("plugin.compose") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("com.android.application") version "8.13.2"
        id("io.insert-koin.compiler.plugin") version version("compilerPlugin")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "snippet-tests-android"
// One project per scenario in ../fixtures-android.
file("../fixtures-android").listFiles()!!
    .filter { it.isDirectory && !it.name.startsWith("_") }
    .forEach { include(it.name) }
