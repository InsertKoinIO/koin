@file:Suppress("UnstableApiUsage")

package koin.jvm

import koin.AbstractKoinPlugin
import koin.DependencyManagementPlugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

class KoinBaseJvmPlugin : AbstractKoinPlugin() {

    override fun apply(target: Project): Unit = target.run {
        apply<KotlinPluginWrapper>()
        apply<DependencyManagementPlugin>()

        applyCheckstyle()
        addSourceJarTask {
            target.extensions.getByType(SourceSetContainer::class.java)["main"].allSource
        }
        addDokkaTask()

        dependencies {
            "api"(kotlin("stdlib"))
        }

        apply<PublishJvmPlugin>()
    }
}
