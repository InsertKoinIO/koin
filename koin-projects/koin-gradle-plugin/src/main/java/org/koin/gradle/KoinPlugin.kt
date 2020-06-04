package org.koin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
import org.gradle.api.tasks.testing.Test

open class KoinPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("checkAndroidModules") { test ->
            test.dependsOn("testDebugUnitTest")
        }

        project.tasks.register("checkModules", Test::class.java) { test: Test? ->
            try {
                test?.let {
                    test.testLogging {
                        it.showStandardStreams = true
                    }
                    test.useJUnit {
                        it.includeCategories("org.koin.test.category.CheckModuleTest")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        project.gradle.taskGraph.whenReady { taskGraph ->
            try {
                val testTask = project.tasks.getByName("testDebugUnitTest") as? Test?
                testTask?.let {
                    if (taskGraph.hasTask(project.tasks.getByName("checkAndroidModules"))) {
                        testTask.testLogging {
                            it.showStandardStreams = true
                        }
                        testTask.useJUnit {
                            it.includeCategories("org.koin.test.category.CheckModuleTest")
                        }
                    } else {
                        System.err.println("can't register checkAndroidModules")
                    }
                }
            } catch (e: UnknownTaskException) {
                // nothing to do with testDebugUnitTest
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}