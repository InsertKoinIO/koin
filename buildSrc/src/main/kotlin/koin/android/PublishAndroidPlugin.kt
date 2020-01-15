@file:Suppress("UnstableApiUsage")

package koin.android

import digital.wup.android_maven_publish.AndroidMavenPublishPlugin
import koin.AbstractKoinPublishPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.get

class PublishAndroidPlugin : AbstractKoinPublishPlugin() {

    override fun apply(target: Project): Unit = target.run {
        apply<AndroidMavenPublishPlugin>()

        configurePublicationComponents(target.components["android"])

        configureBintray()
    }
}
