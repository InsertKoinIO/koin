apply(from = rootDir.resolve("gradle/idea-module-root.gradle.kts"))

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
}

subprojects {
    repositories {
        jcenter()
        google()
        maven(url = "https://dl.bintray.com/kotlin/kotlinx")
        maven(url = "https://dl.bintray.com/kotlin/ktor")
    }
}
