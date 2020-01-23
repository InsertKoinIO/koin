apply(from = rootDir.resolve("gradle/idea-module-root.gradle.kts"))

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
}

subprojects {
    repositories {
        google()
        jcenter()
    }
}