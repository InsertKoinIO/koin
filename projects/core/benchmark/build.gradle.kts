import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    kotlin("plugin.allopen") version "2.2.21"
    alias(libs.plugins.benchmark)
}

kotlin {
    
    jvm()
    macosX64()
    macosArm64()


    sourceSets {
        commonMain.dependencies {
            implementation(libs.benchmark.runtime)
            api(project(":core:koin-core"))
            api(project(":core:koin-core-coroutines"))
        }
        jvmMain.dependencies {

        }
        nativeMain.dependencies {
            
        }
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    configurations {
        // KTZ-4829: isolates the three-timer split from the historical rows. kotlinx-benchmark 0.4.16
        // exposes no JMH profiler passthrough, so allocations per op are measured separately by
        // AllocationProbe (see jvmMain), which reads getThreadAllocatedBytes directly.
        register("perfs") {
            include("GetAllBenchmark")
            warmups = 3
            iterations = 5
            iterationTime = 1
            iterationTimeUnit = "s"
        }
    }
    targets {
        register("jvm")
        register("macosX64")
        register("macosArm64")
    }
}

// KTZ-4829: bytes-per-op probe. See AllocationProbe.kt for why this is not a JMH `-prof gc` run.
tasks.register<JavaExec>("allocProbe") {
    group = "benchmark"
    description = "Reports B/op and ns/op for the KTZ-4829 three-timer split"
    val jvmMain = kotlin.jvm().compilations.getByName("main")
    classpath = files(jvmMain.output.allOutputs, jvmMain.runtimeDependencyFiles)
    mainClass.set("org.koin.benchmark.AllocationProbeKt")
}
