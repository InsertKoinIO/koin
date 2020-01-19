import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

apply(rootDir.resolve("gradle/target-android.gradle.kts"))

description = "Koin project - $name"

val androidXLibVersion: String by extra
val androidXArchVersion: String by extra

configure<KotlinMultiplatformExtension> {
    sourceSets {
        named("androidMain") {
            dependencies {
                api(project(":koin-android"))

                implementation("androidx.appcompat:appcompat:$androidXLibVersion")
                implementation("androidx.lifecycle:lifecycle-common:$androidXArchVersion") {
                    exclude(group = "core-runtime")
                }
            }
        }
    }
}
