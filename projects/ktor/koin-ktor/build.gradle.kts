import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()

    js(IR) {
        nodejs()
        browser()
        binaries.executable()
    }

    wasmJs {
        nodejs()
        binaries.executable()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()
    watchosX64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    mingwX64()
    linuxX64()
    linuxArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":core:koin-core"))
            // Ktor
            implementation(libs.ktor.core)
            //TODO Ktor 3.2
//            implementation(libs.ktor.core.di)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.test.junit)

            implementation(libs.ktor.core)
            implementation(libs.ktor.netty)
            implementation(libs.ktor.testHost)
        }
    }
}


tasks.withType<KotlinCompile>().all {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

apply(from = file("../../gradle/publish.gradle.kts"))

//plugins {
//    kotlin("jvm")
//}
//
//dependencies {
//    api(project(":core:koin-core"))
//    testImplementation(libs.kotlin.test)
//    testImplementation(libs.test.junit)
//
//    // Ktor
//    compileOnly(libs.ktor.core)
//    compileOnly(libs.ktor.core.di)
//    testImplementation(libs.ktor.core)
//    testImplementation(libs.ktor.netty)
//    testImplementation(libs.ktor.testHost)
//}
//
//tasks.withType<KotlinCompile>().all {
//    compilerOptions {
//        jvmTarget.set(JvmTarget.JVM_1_8)
//    }
//}
//java {
//    sourceCompatibility = JavaVersion.VERSION_1_8 // or the desired Java version
//    targetCompatibility = JavaVersion.VERSION_1_8 // or the desired Java version
//}
//val sourcesJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
//    archiveClassifier.set("sources")
//    from(sourceSets.main.map { it.allSource.sourceDirectories })
//}
//
//apply(from = file("../../gradle/publish-java.gradle.kts"))
