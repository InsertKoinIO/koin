package se.thinkcode

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules

open class CheckModulesTask : DefaultTask() {
    @TaskAction
    fun greet(type: JavaExec) {
        val extension = project.extensions.findByType(KoinPluginExtension::class.java)

        extension?.let {
            println("-- Koin Gradle Plugin --")
            val modules = extension.modules
            modules.forEach { module ->
                println("module: $module")
                val clazz = Class.forName(module)
                println("clazz:$clazz")
            }
        }
    }
}