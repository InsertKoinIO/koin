// Versions come from the single versions file of the site, see README.md.
pluginManagement {
    val versions = file("../src/data/versions.ts").readText()
    fun version(key: String) = Regex("""$key: '([^']+)'""").find(versions)!!.groupValues[1]

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version version("kotlinMin")
        id("io.insert-koin.compiler.plugin") version version("compilerPlugin")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "snippet-tests"
// One project per fixture folder.
file("fixtures").listFiles()!!.filter { it.isDirectory }.forEach { include(it.name) }
