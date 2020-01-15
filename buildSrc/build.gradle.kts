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
            implementationClass = "koin.jvm.KoinBaseJvmPlugin"
        }
        register("koin-base-android") {
            id = "koin-base-android"
            implementationClass = "koin.android.KoinBaseAndroidPlugin"
        }
    }
}

val kotlinVersion: String by extra

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:0.7")

    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.4.0")

    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.10.0")

    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")

    // Android
    implementation("com.android.tools.build:gradle:3.5.3")
    implementation("digital.wup:android-maven-publish:3.6.3")
}
