import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

apply(rootDir.resolve("gradle/target-android.gradle.kts"))

description = "Koin project - $name"

val androidXFragmentVersion: String by extra

configure<KotlinMultiplatformExtension> {
    sourceSets {
        named("androidMain") {
            dependencies {
                api(project(":koin-android"))

                implementation("androidx.fragment:fragment:$androidXFragmentVersion")
            }
        }
    }
}
