// Multiplatform scenarios, kept apart from the JVM projects because they need the Android SDK.
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
        kotlin("multiplatform") version kotlinVersion
        kotlin("plugin.compose") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("com.android.library") version "8.13.2"
        id("org.jetbrains.compose") version version("composeMultiplatform")
        id("io.insert-koin.compiler.plugin") version version("compilerPlugin")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "snippet-tests-kmp"
// One project per scenario in ../fixtures-kmp.
file("../fixtures-kmp").listFiles()!!.filter { it.isDirectory }.forEach { include(it.name) }
