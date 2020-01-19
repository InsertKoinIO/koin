import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

apply(rootDir.resolve("gradle/target-android.gradle.kts"))

description = "Koin project - $name"

val androidXArchVersion: String by extra
val androidXSavedStateVersion: String by extra

configure<KotlinMultiplatformExtension> {
    sourceSets {
        named("androidMain") {
            dependencies {
                api(project(":koin-core-ext"))
                api(project(":koin-androidx-viewmodel"))

                implementation("androidx.lifecycle:lifecycle-extensions:$androidXArchVersion") {
                    exclude(module = "lifecycle-livedata")
                    exclude(module = "lifecycle-service")
                    exclude(module = "lifecycle-process")
                    exclude(module = "runtime")
                    exclude(group = "androidx.legacy")
                }

                implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$androidXSavedStateVersion") {
                    exclude(module = "lifecycle-livedata")
                    exclude(module = "lifecycle-service")
                    exclude(module = "lifecycle-process")
                    exclude(module = "runtime")
                    exclude(group = "androidx.legacy")
                }
            }
        }
    }
}
