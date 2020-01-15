@file:Suppress("UnstableApiUsage")

package koin.jvm

import koin.AbstractKoinPublishPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

class PublishJvmPlugin : AbstractKoinPublishPlugin() {

    override fun apply(target: Project): Unit = target.run {
        configurePublicationComponents(target.components["java"])

        configureBintray()
    }
}
