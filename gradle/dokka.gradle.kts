@file:Suppress("UnstableApiUsage")

import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

apply<DokkaPlugin>()

val dokkaTask by tasks.named("dokka", DokkaTask::class) {
    impliedPlatforms = mutableListOf("common")

    configuration {
        reportUndocumented = true
    }
}

val assembleTask by tasks.named(ASSEMBLE_TASK_NAME)

configure<KotlinMultiplatformExtension> {
    targets.forEach { target ->
        when (target) {
            is KotlinJvmTarget     -> run {
                val jvmDokkaJarTask by tasks.registering(Jar::class) {
                    from(dokkaTask.outputDirectory)
                    dependsOn(dokkaTask)
                    archiveBaseName.set("${archiveBaseName.get()}-${target.name}")
                    archiveClassifier.set("javadoc")
                }
                assembleTask.dependsOn(jvmDokkaJarTask)
            }

            is KotlinAndroidTarget -> run {
                // FIXME When you turn on the task, the project ceases to assemble. It is necessary to fix the work for Android modules
                dokkaTask.enabled = false
            }
        }
    }
}
