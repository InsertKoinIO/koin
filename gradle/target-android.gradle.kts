@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

apply(rootDir.resolve("gradle/target-common.gradle.kts"))
apply<LibraryPlugin>()
apply<KotlinMultiplatformPluginWrapper>()

val androidMinVersion: String by extra
val androidTargetVersion: String by extra
val androidBuildToolsVersion: String by extra

val javaVersion = JavaVersion.VERSION_1_8

configure<LibraryExtension> {
    compileSdkVersion(androidTargetVersion.toInt())
    buildToolsVersion(androidBuildToolsVersion)
    defaultConfig {
        minSdkVersion(androidMinVersion)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("src/androidMain/kotlin")
            res.srcDirs("src/androidMain/res")
        }

        named("androidTest") {
            java.srcDirs("src/androidTest/kotlin")
            res.srcDirs("src/androidTest/res")
        }
    }
}

val kotlinVersion: String by extra
val mockitoVersion: String by extra

configure<KotlinMultiplatformExtension> {
    android("android") {
        publishLibraryVariants("release")
    }

    sourceSets {
        named("androidMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8", kotlinVersion))
            }
        }

        named("androidTest") {
            dependencies {
                implementation(kotlin("test", kotlinVersion))
                implementation(kotlin("test-junit", kotlinVersion))
                implementation("org.mockito:mockito-inline:$mockitoVersion")

                implementation(project(":koin-test"))
            }
        }
    }
}

//configurations.maybeCreate("compileClasspath")

afterEvaluate {
    tasks {
//        withType<DokkaTask> {
//            impliedPlatforms = mutableListOf("Common")
//        }
//
//        val sourcesJarTasks = filter { it.name.endsWith("ReleaseSourcesJar") }
//
//        val androidDokkaJar by registering(Jar::class) {
//            val dokkaTask = getByName<DokkaTask>("dokka")
//            from(dokkaTask.outputDirectory)
//            dependsOn(dokkaTask)
//            archiveBaseName.set("${archiveBaseName.get()}-android")
//            archiveClassifier.set("javadoc")
//        }
//
//        getByName(ASSEMBLE_TASK_NAME) {
//            dependsOn(sourcesJarTasks, androidDokkaJar)
//        }
    }
}

apply(rootDir.resolve("gradle/dokka.gradle.kts"))
apply(rootDir.resolve("gradle/publish.gradle.kts"))
