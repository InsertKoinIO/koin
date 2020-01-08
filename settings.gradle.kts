//include(":koin-core")

@Suppress("ktNoinlineFunc")
fun includeOldModule(name: String) {
    include(name)
    project(":$name").projectDir = rootDir.resolve("koin-projects/$name")
}

includeOldModule("koin-core")
