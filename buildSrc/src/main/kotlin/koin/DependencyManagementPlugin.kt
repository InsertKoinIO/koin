package koin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

class DependencyManagementPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        repositories {
            jcenter()
            google()
            maven(url = "https://dl.bintray.com/kotlin/kotlinx")
            maven(url = "https://dl.bintray.com/kotlin/ktor")
        }

        configurations.all {
            resolutionStrategy {
                failOnVersionConflict()
                preferProjectModules()

                eachDependency {
                    when (requested.group) {
                        "org.jetbrains.kotlin"  -> useVersion(getKotlinPluginVersion()!!)
                        "org.jetbrains.kotlinx" -> when {
                            requested.name.startsWith("kotlinx-coroutines-io") -> useVersion("0.1.16")
                            requested.name.startsWith("kotlinx-coroutines-")   -> useVersion("1.3.0")
                        }

                        "junit"                 -> useVersion("4.12")
                        "org.mockito"           -> useVersion("2.21.0")
                        "org.slf4j"             -> useVersion("1.7.30")
                        "io.ktor"               -> useVersion("1.2.6")

                    }
                }
            }
        }
    }
}