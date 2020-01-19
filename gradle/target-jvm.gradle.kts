@file:Suppress("UnstableApiUsage")

import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

apply(rootDir.resolve("gradle/target-common.gradle.kts"))
apply<KotlinMultiplatformPluginWrapper>()

val kotlinVersion: String by extra
val coroutinesVersion: String by extra
val mockitoVersion: String by extra

configure<KotlinMultiplatformExtension> {
    jvm {
        sourceSets {
            named("jvmMain") {
                dependencies {
                    api(kotlin("stdlib-jdk8", kotlinVersion))
                }
            }

            named("jvmTest") {
                dependencies {
                    implementation(kotlin("test", kotlinVersion))
                    implementation(kotlin("test-junit", kotlinVersion))
                    implementation("org.mockito:mockito-inline:$mockitoVersion")
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                }
            }
        }
    }
}

tasks {
    val sourcesJarTasks = filter { it.name.endsWith("SourcesJar") }

    named(ASSEMBLE_TASK_NAME) {
        dependsOn(sourcesJarTasks)
    }
}

apply(rootDir.resolve("gradle/dokka.gradle.kts"))
apply(rootDir.resolve("gradle/publish.gradle.kts"))
