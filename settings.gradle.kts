//include(":koin-core")

@Suppress("ktNoinlineFunc")
fun includeOldModule(name: String) {
    include(name)
    project(":$name").projectDir = rootDir.resolve("koin-projects/$name")
}

// Core
includeOldModule("koin-core")
includeOldModule("koin-test")
// Core extended
includeOldModule("koin-core-ext")

// Ktor
includeOldModule("koin-logger-slf4j")
