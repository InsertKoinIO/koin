package org.koin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

open class KoinPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("checkModules", Test::class.java) { test: Test? ->
            if (test != null) {
                test.testLogging {
                    it.showStandardStreams = true
                }
                test.useJUnit {
                    it.includeCategories("org.koin.test.category.CheckModuleTest")
                }
            } else {
                System.err.println("can't register checkModules")
            }
        }
    }
}