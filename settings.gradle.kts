// Core
include("koin-core")
include("koin-test")
// Core Sample
include("examples:coffee-maker")
// Core extended
include("koin-core-ext")

// Android
include("koin-android")
include("koin-android-scope")
include("koin-android-viewmodel")
include("koin-android-ext")

// AndroidX
include("koin-androidx-scope")
include("koin-androidx-fragment")
include("koin-androidx-viewmodel")
include("koin-androidx-ext")

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
