@file:Suppress("UnstableApiUsage")

import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

apply<KotlinMultiplatformPluginWrapper>()

val kotlinVersion: String by extra
val junitVersion: String by extra
val mockitoVersion: String by extra
val coroutinesVersion: String by extra

configure<KotlinMultiplatformExtension> {
    metadata {
        sourceSets {
            named("commonMain") {
                dependencies {
                    api(kotlin("stdlib", kotlinVersion))
                }
            }
        }
    }
}

apply(from = rootDir.resolve("gradle/checkstyle.gradle.kts"))
apply(from = rootDir.resolve("gradle/dokka.gradle.kts"))
apply(from = rootDir.resolve("gradle/publish.gradle.kts"))