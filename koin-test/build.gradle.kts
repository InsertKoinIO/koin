description = "Koin - simple dependency injection for Kotlin - $name"

apply(from = rootDir.resolve("gradle/target-jvm.gradle.kts"))

val junitVersion: String by extra
val mockitoVersion: String by extra

configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
    jvm {
        sourceSets {
            named("jvmMain") {
                dependencies {
                    api(project(":koin-core"))

                    api("junit:junit:$junitVersion")
                    implementation("org.mockito:mockito-inline:$mockitoVersion")
                }
            }
        }
    }
}