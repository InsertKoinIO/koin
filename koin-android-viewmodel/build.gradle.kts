import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

apply(rootDir.resolve("gradle/target-android.gradle.kts"))

description = "Koin project - $name"

val androidArchVersion: String by extra

configure<KotlinMultiplatformExtension> {
    sourceSets {
        named("androidMain") {
            dependencies {
                api(project(":koin-android-scope"))

                implementation("android.arch.lifecycle:extensions:$androidArchVersion") {
                    exclude(module = "livedata")
                    exclude(module = "runtime")
                }
            }
        }
    }
}
