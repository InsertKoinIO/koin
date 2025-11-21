import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

repositories.mavenCentral()

apply(from = "../gradle/versions.gradle")

plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("kapt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

val jmhVersion = "1.36"
val koinVersion = extra["koin_version"] as String

dependencies {
    api("io.insert-koin:koin-core:$koinVersion")
    api("io.insert-koin:koin-core-coroutines:$koinVersion")
    api("io.insert-koin:koin-fu:$koinVersion")
    implementation("org.openjdk.jmh:jmh-core:$jmhVersion")
    kapt("org.openjdk.jmh:jmh-generator-annprocess:$jmhVersion")
}

task<JavaExec>("runBenchmark") {
    dependsOn("classes")
    mainClass.set("org.openjdk.jmh.Main")
    classpath = sourceSets.main.get().runtimeClasspath
}
