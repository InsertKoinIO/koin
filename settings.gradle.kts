@Suppress("ktNoinlineFunc")
fun includeOldModule(name: String) {
    include(name)
    project(":$name").projectDir = rootDir.resolve("koin-projects/${name.replace(":", "/")}")
    project(":$name").name = name.replace(":", "-")
}

// Core
include("koin-core")
include("koin-test")
// Core Sample
include("examples:coffee-maker")
// Core extended
include("koin-core-ext")

// Ktor
include("koin-logger-slf4j")
include("koin-ktor")
// Ktor Sample
include("examples:hello-ktor")

include("examples:multimodule-ktor")
include("examples:multimodule-ktor:common")
include("examples:multimodule-ktor:module-a")
include("examples:multimodule-ktor:module-b")
include("examples:multimodule-ktor:app")
