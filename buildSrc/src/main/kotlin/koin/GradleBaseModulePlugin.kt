@file:Suppress("UnstableApiUsage")

package koin

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

class GradleBaseModulePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        apply<KotlinPluginWrapper>()
        apply<DependencyManagementPlugin>()
        apply<DokkaPlugin>()
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

        val sourcesJar by tasks.registering(Jar::class) {
            archiveClassifier.set("sources")
            from(target.extensions.getByType(SourceSetContainer::class.java)["main"].allSource)
        }
        val dokka by tasks.getting(DokkaTask::class) {
            outputDirectory = "$buildDir/javadoc"
        }

        val dokkaJar by tasks.registering(Jar::class) {
            archiveClassifier.set("javadoc")
            from(dokka)
        }

        tasks.named(ASSEMBLE_TASK_NAME) {
            dependsOn(sourcesJar, dokkaJar)
        }

        dependencies {
            "api"(kotlin("stdlib"))
        }

        apply<PublishPlugin>()
    }
}
