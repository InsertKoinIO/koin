@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation

apply<DetektPlugin>()

val detekt by tasks.getting(Detekt::class)

configure<KotlinMultiplatformExtension> {
    targets.forEach { target ->
        target.compilations.forEach { compilation ->
            when (compilation) {
                is KotlinJvmCompilation -> run {
                    tasks.maybeCreate("${target.name}${compilation.name.capitalize()}Detekt", Detekt::class).apply {
                        group = VERIFICATION_GROUP
                        detekt.dependsOn(this)
                        source = compilation.defaultSourceSet.kotlin.asFileTree
                    }
                }
            }
        }
    }
}

tasks.withType(Detekt::class) {
    config.setFrom(files(rootDir.resolve("gradle/detekt.yml")))
    parallel = true
    buildUponDefaultConfig = true
    ignoreFailures = false
    reports {
        txt { enabled = false }
    }
}
