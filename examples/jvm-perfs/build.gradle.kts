import org.jetbrains.kotlin.gradle.dsl.JvmTarget

repositories.mavenCentral()

plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("kapt")
}

val jvmTarget = "17"

tasks.getByName<JavaCompile>("compileJava") {
    targetCompatibility = jvmTarget
}

tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

val jmhVersion = "1.36"
//TODO get from existing version.gradle file
val koin_version = "4.0.0"

dependencies {
    api("io.insert-koin:koin-core:$koin_version")
    api("io.insert-koin:koin-core-coroutines:$koin_version")
    api("io.insert-koin:koin-fu:$koin_version")
    implementation("org.openjdk.jmh:jmh-core:$jmhVersion")
    kapt("org.openjdk.jmh:jmh-generator-annprocess:$jmhVersion")
}

task<JavaExec>("runBenchmark") {
    dependsOn("classes")
    mainClass.set("org.openjdk.jmh.Main")
    classpath = sourceSets.main.get().runtimeClasspath
}