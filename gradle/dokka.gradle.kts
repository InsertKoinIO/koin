@file:Suppress("UnstableApiUsage")

import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

apply<DokkaPlugin>()

val dokkaTask by tasks.named("dokka", DokkaTask::class) {
    configuration {
        reportUndocumented = true
        jdkVersion = 8
    }
}

val assembleTask by tasks.named(ASSEMBLE_TASK_NAME)

val dokkaJar by tasks.registering(Jar::class) {
    from(dokkaTask.outputDirectory)
    dependsOn(dokkaTask)
    archiveClassifier.set("javadoc")
}
assembleTask.dependsOn(dokkaJar)
