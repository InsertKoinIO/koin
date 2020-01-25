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

val kotlinVersion: String by extra
val dokkaVersion: String by extra

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:0.7")

    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.4.0")

    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")

    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")

    // Android
    implementation("com.android.tools.build:gradle:3.5.3")
    implementation("digital.wup:android-maven-publish:3.6.3")
}
