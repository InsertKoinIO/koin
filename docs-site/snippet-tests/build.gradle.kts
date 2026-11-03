// Each project compiles the code in .generated/<project>, written by
// scripts/extract-snippets.mjs, then run it with `gradle run`.
plugins {
    kotlin("jvm") apply false
    id("io.insert-koin.compiler.plugin") apply false
}

val versions = file("../src/data/versions.ts").readText()
fun version(key: String) = Regex("""$key: '([^']+)'""").find(versions)!!.groupValues[1]

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "application")
    if (name.startsWith("with-plugin")) apply(plugin = "io.insert-koin.compiler.plugin")

    configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
        jvmToolchain(17)
        sourceSets.named("main") {
            kotlin.srcDir(rootProject.file(".generated/${project.name}"))
        }
    }
    configure<JavaApplication> {
        mainClass.set("RunnerKt")
    }

    dependencies {
        "implementation"(platform("io.insert-koin:koin-bom:${version("koin")}"))
        "implementation"("io.insert-koin:koin-ktor")
        "implementation"("io.insert-koin:koin-annotations")
        "implementation"("io.insert-koin:koin-test")
        "implementation"("org.jetbrains.kotlin:kotlin-test-junit")
        "implementation"("io.insert-koin:koin-logger-slf4j")
        "implementation"("io.ktor:ktor-server-core:${version("ktor")}")
        "implementation"("io.ktor:ktor-server-di:${version("ktor")}")
        "implementation"("io.ktor:ktor-server-netty:${version("ktor")}")
        "implementation"("io.ktor:ktor-server-test-host:${version("ktor")}")
        "implementation"("ch.qos.logback:logback-classic:${version("logback")}")
        // The connection pool recipe of ktor/configuration.mdx.
        "implementation"("com.zaxxer:HikariCP:6.3.0")
        "implementation"("com.h2database:h2:2.3.232")
    }
}
