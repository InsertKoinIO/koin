@file:Suppress("UnstableApiUsage", "ktNoinlineFunc")

package koin

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

abstract class AbstractKoinPlugin : Plugin<Project> {
    protected fun Project.applyCheckstyle() {
        apply<DetektPlugin>()
        configure<DetektExtension> {
            config = files(rootDir.resolve("gradle/detekt.yml"))
            parallel = true
            buildUponDefaultConfig = true
            ignoreFailures = false
            reports {
                txt { enabled = false }
            }
        }
    }

    protected fun Project.applyDokkaTask() {
        apply<DokkaPlugin>()
        val dokka by tasks.getting(DokkaTask::class) {
            outputDirectory = "$buildDir/javadoc"
        }

        val dokkaJar by tasks.registering(Jar::class) {
            archiveClassifier.set("javadoc")
            from(dokka)
        }
        tasks.named(ASSEMBLE_TASK_NAME) {
            dependsOn(dokkaJar)
        }
    }

    protected fun Project.addSourceJarTask(block: Jar.() -> Unit) {
        val sourcesJar by tasks.registering(Jar::class) {
            archiveClassifier.set("sources")
            block(this)
        }
        tasks.named(ASSEMBLE_TASK_NAME) {
            dependsOn(sourcesJar)
        }
    }

}
