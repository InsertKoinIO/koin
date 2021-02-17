plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    google()
}

dependencies {

    implementation(project(":koin-core"))

    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.4.30-1.0.0-alpha02")
    implementation("com.squareup:javapoet:1.12.1")
}
