@file:Suppress("UnstableApiUsage", "ktNoinlineFunc")

package koin.android

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import koin.AbstractKoinPlugin
import koin.DependencyManagementPlugin
import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

class KoinBaseAndroidPlugin : AbstractKoinPlugin() {
    override fun apply(target: Project): Unit = target.run {
        apply<DependencyManagementPlugin>()
        apply<LibraryPlugin>()
        apply<KotlinAndroidPluginWrapper>()

        val androidMinVersion: String by extra
        val androidTargetVersion: String by extra
        val androidBuildToolsVersion: String by extra

        configure<LibraryExtension> {
            compileSdkVersion(androidTargetVersion.toInt())
            buildToolsVersion(androidBuildToolsVersion)
            defaultConfig {
                minSdkVersion(androidMinVersion)
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
            compileOptions {
                sourceCompatibility = VERSION_1_8
                targetCompatibility = VERSION_1_8
            }
        }

        applyCheckstyle()
        addSourceJarTask {
            from(target.the<LibraryExtension>().sourceSets["main"].java.sourceFiles)
        }
        // TODO addDokkaTask()

        apply<PublishAndroidPlugin>()
    }
}
