import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":core:koin-core"))
    testImplementation(libs.kotlin.test)
    testImplementation(libs.test.junit)

    // Ktor
    api(libs.ktor.core)
    testImplementation(libs.ktor.netty)
    testImplementation(libs.ktor.testHost)
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "11"
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_11 // or the desired Java version
    targetCompatibility = JavaVersion.VERSION_11 // or the desired Java version
}
val sourcesJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.map { it.allSource.sourceDirectories })
}

apply(from = file("../../gradle/publish-java.gradle.kts"))
