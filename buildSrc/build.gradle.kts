plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    gradlePluginPortal()
    jcenter()
    google()
}

gradlePlugin {
    plugins {
        register("koin-versions") {
            id = "koin-versions"
            implementationClass = "koin.DependencyManagementPlugin"
        }
    }
}

apply(from = "src/main/kotlin/versions.gradle.kts")

val kotlinVersion: String by extra

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))

    api("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:0.7")
}
