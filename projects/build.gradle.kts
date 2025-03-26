import io.gitlab.arturbosch.detekt.Detekt

plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.dokka).apply(false)
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinBinary)
    alias(libs.plugins.nexusPublish)
    alias(libs.plugins.detekt)
}

fun getRepositoryUsername(): String =
    findProperty("OSSRH_USERNAME")?.toString() ?: System.getenv("OSSRH_USERNAME") ?: ""

fun getRepositoryPassword(): String =
    findProperty("OSSRH_PASSWORD")?.toString() ?: System.getenv("OSSRH_PASSWORD") ?: ""

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            username.set(getRepositoryUsername())
            password.set(getRepositoryPassword())
        }
    }
}

allprojects {

    val koinVersion: String by project

    group = "io.insert-koin"
    version = koinVersion

    apply(plugin = "org.jetbrains.dokka")
    val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)
    val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        toolVersion = "1.23.8"
//        config.setFrom(file("config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
    }

    tasks.withType<Detekt>().configureEach {
        reports {
            xml.required.set(false)
            html.required.set(true)
            sarif.required.set(false)
            md.required.set(true)
        }
    }
}