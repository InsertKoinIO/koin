@file:Suppress("UnstableApiUsage")

package koin

import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import useDependencyResolvedVersions

class PublishPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        apply<KotlinPluginWrapper>()
        apply<MavenPlugin>()
        apply<MavenPublishPlugin>()
        apply<BintrayPlugin>()

        configure<PublishingExtension> {
            publications {
                register("mavenJava", MavenPublication::class) {
                    from(components["java"])
                    artifact(target.tasks.getByName("sourcesJar", Jar::class))
                    artifact(target.tasks.getByName("dokkaJar", Jar::class))
                    pom {
                        useDependencyResolvedVersions(configurations)
                    }
                }
            }
        }

        (extra["bintray_user"] as? String)?.let { bintrayUser ->
            (extra["bintray_apikey"] as? String)?.let { bintrayApiKey ->
                configure<BintrayExtension> {
                    user = bintrayUser
                    key = bintrayApiKey
                    override = true
                    publish = true
                    setPublications("mavenJava")
                    pkg.apply {
                        repo = "koin"
                        name = target.name
                        userOrg = "ekito"
                        setLicenses("Apache-2.0")
                        vcsUrl = "https://github.com/Ekito/koin.git"
                    }
                }
            }
        }
    }
}
