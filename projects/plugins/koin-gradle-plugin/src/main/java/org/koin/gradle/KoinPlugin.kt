/*
 * Copyright 2017-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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