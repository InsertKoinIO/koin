import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":core:koin-core"))
    api(libs.ktor.slf4j)
}

tasks.withType<KotlinCompile>().all {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}
val sourcesJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.map { it.allSource.sourceDirectories })
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8 // or the desired Java version
    targetCompatibility = JavaVersion.VERSION_1_8 // or the desired Java version
}
apply(from = file("../../gradle/publish-java.gradle.kts"))
