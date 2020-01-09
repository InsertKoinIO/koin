@Suppress("ktNoinlineFunc")
fun includeOldModule(name: String) {
    include(name)
    project(":$name").projectDir = rootDir.resolve("koin-projects/${name.replace(":", "/")}")
    project(":$name").name = name.replace(":", "-")
}

// Core
includeOldModule("koin-core")
includeOldModule("koin-test")
// Core Sample
includeOldModule("examples:coffee-maker")
// Core extended
includeOldModule("koin-core-ext")

// Ktor
includeOldModule("koin-logger-slf4j")
includeOldModule("koin-ktor")
// Ktor Sample
includeOldModule("examples:hello-ktor")

includeOldModule("examples:multimodule-ktor")
includeOldModule("examples:multimodule-ktor:common")
includeOldModule("examples:multimodule-ktor:module-a")
includeOldModule("examples:multimodule-ktor:module-b")
includeOldModule("examples:multimodule-ktor:app")
