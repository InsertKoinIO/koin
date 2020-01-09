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
        register("koin-base-jvm") {
            id = "koin-base-jvm"
            implementationClass = "koin.GradleBaseModulePlugin"
        }
    }
}

val kotlinVersion: String by extra
val bintrayVersion: String by extra
val dokkaVersion: String by extra

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))

    api("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:0.7")
    api("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
    api("com.jfrog.bintray.gradle:gradle-bintray-plugin:$bintrayVersion")
}
