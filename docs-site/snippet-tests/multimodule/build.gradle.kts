// The Gradle setup of android/multi-module.mdx. Sources come from .generated/multimodule/<style>/<module>,
// written by scripts/extract-snippets.mjs. Keep the dependencies in sync with the page.
plugins {
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("io.insert-koin.compiler.plugin") apply false
}

val versions = file("../../src/data/versions.ts").readText()
fun version(key: String) = Regex("""$key: '([^']+)'""").find(versions)!!.groupValues[1]

// What each Gradle module depends on, as the page shows it.
val moduleDependencies = mapOf(
    "app" to listOf("feature-login", "feature-profile", "core-data"),
    "feature-login" to listOf("core-domain"),
    "feature-profile" to listOf("core-domain"),
    "core-data" to listOf("core-domain"),
    "core-domain" to emptyList(),
)

for (style in listOf("dsl", "annotations")) for ((module, dependsOn) in moduleDependencies) project(":$style:$module") {
    val generated = rootProject.file("../.generated/multimodule/$style/$module")
    val isApp = module == "app"
    val hasDefinitions = module != "core-domain"

    apply(plugin = if (isApp) "com.android.application" else "com.android.library")
    apply(plugin = "org.jetbrains.kotlin.android")
    if (hasDefinitions) apply(plugin = "io.insert-koin.compiler.plugin")

    (extensions.getByName("android") as com.android.build.api.dsl.CommonExtension<*, *, *, *, *, *>).apply {
        namespace = "org.koin.sample." + module.replace('-', '.')
        compileSdk = 36
        defaultConfig.minSdk = version("androidMinSdk").toInt()
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        testOptions.unitTests.isIncludeAndroidResources = true
        sourceSets.getByName("main") {
            java.srcDir(generated.resolve("main"))
            if (isApp) manifest.srcFile(generated.resolve("AndroidManifest.xml"))
        }
        sourceSets.getByName("test") {
            java.srcDir(generated.resolve("test"))
        }
    }
    configure<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension> {
        jvmToolchain(17)
    }

    dependencies {
        dependsOn.forEach { "implementation"(project(":$style:$it")) }
        if (hasDefinitions) {
            "implementation"(platform("io.insert-koin:koin-bom:${version("koin")}"))
            "implementation"("io.insert-koin:koin-android")
            if (style == "annotations") "implementation"("io.insert-koin:koin-annotations")
        }
        if (isApp) {
            "testImplementation"("junit:junit:4.13.2")
            "testImplementation"("org.robolectric:robolectric:4.16")
            "testImplementation"("androidx.test:core:1.7.0")
        }
    }
}

tasks.register("checkScenario") {
    dependsOn(":dsl:app:testDebugUnitTest", ":annotations:app:testDebugUnitTest")
}
