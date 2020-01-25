@file:Suppress("UnstableApiUsage")

import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMetadataTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

apply<DokkaPlugin>()

val dokkaVersion: String by extra

val dokka by tasks.getting(DokkaTask::class) {
    configuration {
        reportUndocumented = true
        jdkVersion = 8
    }
}

val sourcesJarTasks = tasks.filter { it.name.endsWith("SourcesJar") }
val assembleTask by tasks.named(ASSEMBLE_TASK_NAME) {
    dependsOn(dokka, sourcesJarTasks)
}

if (plugins.hasPlugin(KotlinMultiplatformPlugin::class)) {
    configure<KotlinMultiplatformExtension> {
        targets.forEach { target ->
            when (target) {
                is KotlinMetadataTarget -> KotlinPlatformType.common
                is KotlinJvmTarget -> KotlinPlatformType.jvm
                else -> null
            }?.let { dokkaTarget ->
                dokka.multiplatform {
                    register(dokkaTarget.name) {
                        platform = dokkaTarget.name
                        targets = targets + dokkaTarget.name
                    }
                }
            }
        }
    }
} else {
    val dokkaJar = tasks.maybeCreate("dokkaJar", Jar::class).apply {
        from(dokka.outputDirectory)
        dependsOn(dokka)
        archiveClassifier.set("javadoc")
    }
    assembleTask.dependsOn(dokkaJar)
}