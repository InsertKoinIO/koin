import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

apply<KotlinMultiplatformPluginWrapper>()

val kotlinVersion: String by extra

configure<KotlinMultiplatformExtension> {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(kotlin("stdlib-common", kotlinVersion))
            }
        }

        named("commonTest") {
            dependencies {
                implementation(kotlin("test-common", kotlinVersion))
                implementation(kotlin("test-annotations-common", kotlinVersion))
            }
        }
    }
}
