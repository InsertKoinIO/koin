
plugins {

    id("com.google.devtools.ksp") version  "1.5.10-1.0.0-beta01"
    kotlin("jvm")
}

dependencies{
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":koin-ksp"))
    ksp(project(":koin-ksp"))
}