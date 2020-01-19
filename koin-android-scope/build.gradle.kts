import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

apply(rootDir.resolve("gradle/target-android.gradle.kts"))

description = "Koin project - $name"

val androidSupportLibVersion: String by extra
val androidArchVersion: String by extra

configure<KotlinMultiplatformExtension> {
    sourceSets {
        named("androidMain") {
            dependencies {
                implementation("com.android.support:appcompat-v7:$androidSupportLibVersion")
                implementation("android.arch.lifecycle:common:$androidArchVersion")

                api(project(":koin-android"))
            }
        }
    }
}
