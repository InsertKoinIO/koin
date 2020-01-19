@file:Suppress("ktNoinlineFunc")

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.gradle.ext.RunConfiguration
import org.jetbrains.gradle.ext.RunConfigurationContainer

fun Project.gradleRunConfiguration(
    tasks: List<String>,
    cfgSubName: String = tasks.joinToString(),
    block: Gradle.() -> Unit = {}
) {
    val subProject = project

    rootProject.configure<IdeaModel> {
        project {
            this as ExtensionAware

            configure<ProjectSettings> {
                this as ExtensionAware

                configure<NamedDomainObjectContainer<RunConfiguration>> {
                    this as RunConfigurationContainer
                    val cfgPrefixName = if (subProject == rootProject) "::" else "::${subProject.name}"
                    maybeCreate("$cfgPrefixName [$cfgSubName]", Gradle::class.java).apply {
                        setProject(subProject)
                        taskNames = tasks
                        block()
                    }
                }
            }
        }
    }
}
