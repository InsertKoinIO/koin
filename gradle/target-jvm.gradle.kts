@file:Suppress("UnstableApiUsage")

import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

apply<KotlinPluginWrapper>()

val kotlinVersion: String by extra
val junitVersion: String by extra
val mockitoVersion: String by extra
val coroutinesVersion: String by extra

dependencies {
    "api"(kotlin("stdlib", kotlinVersion))

    "testApi"("junit:junit:$junitVersion")
    "testApi"("org.mockito:mockito-inline:$mockitoVersion")

    "testApi"("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
}

configure<JavaPluginExtension> {
    withSourcesJar()
}

tasks {
    val sourcesJarTasks = filter { it.name.endsWith("SourcesJar") }

    named(ASSEMBLE_TASK_NAME) {
        dependsOn(sourcesJarTasks)
    }
}

apply(from = rootDir.resolve("gradle/checkstyle.gradle.kts"))
apply(from = rootDir.resolve("gradle/dokka.gradle.kts"))
apply(from = rootDir.resolve("gradle/publish.gradle.kts"))