package se.thinkcode

import org.gradle.api.Plugin
import org.gradle.api.Project

open class KoinPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("koin", KoinPluginExtension::class.java)
        project.tasks.create("checkModules", CheckModulesTask::class.java)
    }
}