@file:Suppress("ktNoinlineFunc", "UnstableApiUsage")

package koin

import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.register
import useDependencyResolvedVersions

abstract class AbstractKoinPublishPlugin : Plugin<Project> {

    protected fun Project.configurePublicationComponents(components: SoftwareComponent) {
        apply<MavenPublishPlugin>()
        apply<MavenPlugin>()

        configure<PublishingExtension> {
            publications {
                register(PUBLICATION_NAME, MavenPublication::class) {
                    from(components)
                    tasks.findByName("sourcesJar")?.let { artifact(it) }
                    tasks.findByName("dokkaJar")?.let { artifact(it) }
                    pom {
                        useDependencyResolvedVersions(configurations)
                    }
                }
            }
        }
    }

    protected fun Project.configureBintray() {
        apply<BintrayPlugin>()

        (extra["bintray_user"] as? String)?.let { bintrayUser ->
            (extra["bintray_apikey"] as? String)?.let { bintrayApiKey ->
                val projectName = name
                configure<BintrayExtension> {
                    user = bintrayUser
                    key = bintrayApiKey
                    override = true
                    publish = true
                    setPublications(PUBLICATION_NAME)
                    pkg.apply {
                        userOrg = "ekito"
                        repo = "koin"
                        name = projectName
                        desc = description
                        setLicenses("Apache-2.0")
                        vcsUrl = "https://github.com/ekito/koin.git"
                        websiteUrl = "https://insert-koin.io"
                    }
                }
            }
        }
    }

    companion object {
        private const val PUBLICATION_NAME = "mavenJava"
    }
}
