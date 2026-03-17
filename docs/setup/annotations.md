---
title: Koin Annotations
---

Setup Koin Annotations for your project

## Current Versions

You can find all Koin packages on [maven central](https://search.maven.org/search?q=io.insert-koin).

Here are the currently available Koin Annotations versions:

- **Stable**: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-annotations?label=stable)](https://mvnrepository.com/artifact/io.insert-koin/koin-annotations) - Use for production applications
- **Latest**: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-annotations)](https://mvnrepository.com/artifact/io.insert-koin/koin-annotations) - Preview of upcoming features

## KSP Plugin

We need the [Google KSP](https://github.com/google/ksp) to work. Follow the official [KSP Setup documentation](https://kotlinlang.org/docs/ksp-quickstart.html).

Just add the Gradle plugin:
```kotlin
plugins {
    id("com.google.devtools.ksp") version "$ksp_version"
}
```

**KSP Compatibility**: Koin Annotations 2.3.1 requires KSP `2.3.2`

:::info
**KSP Versioning Change**: Starting from KSP 2.x, the version numbering is now independent from Kotlin versions. Use KSP 2.3.2 for Koin Annotations 2.3.1.
:::

## Using Version Catalogs (Recommended)

In your `gradle/libs.versions.toml`:

```toml
[versions]
koin-annotations = "2.3.1"  # Stable version
ksp = "2.3.2"  # Required for Koin Annotations 2.3.1

[libraries]
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin-annotations" }
koin-ksp-compiler = { module = "io.insert-koin:koin-ksp-compiler", version.ref = "koin-annotations" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

## Android & Ktor App KSP Setup

- use KSP Gradle plugin
- add dependency for koin annotations and koin ksp compiler
- set sourceSet

```kotlin
plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    // Koin
    implementation("io.insert-koin:koin-android:$koin_version")
    // Koin Annotations
    implementation("io.insert-koin:koin-annotations:$koin_annotations_version")
    // Koin Annotations KSP Compiler
    ksp("io.insert-koin:koin-ksp-compiler:$koin_annotations_version")
}
```

Or with version catalogs:

```kotlin
plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    // Koin
    implementation(libs.koin.android)
    // Koin Annotations
    implementation(libs.koin.annotations)
    // Koin Annotations KSP Compiler
    ksp(libs.koin.ksp.compiler)
}
```

## Kotlin Multiplatform Setup

In a standard Kotlin/Kotlin Multiplatform project, you need to setup KSP as follow:

- use KSP Gradle plugin
- add dependency in commonMain for koin annotations
- set sourceSet for commonMain
- add KSP dependencies tasks with koin compiler
- setup compilation task dependency to `kspCommonMainKotlinMetadata`

```kotlin
plugins {
    alias(libs.plugins.ksp)
}

kotlin {

    sourceSets {

        // Add Koin Annotations
        commonMain.dependencies {
            // Koin
            implementation("io.insert-koin:koin-core:$koin_version")
            // Koin Annotations
            api("io.insert-koin:koin-annotations:$koin_annotations_version")
        }
    }

    // KSP Common sourceSet
    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}

// KSP Tasks
dependencies {
    add("kspCommonMainMetadata", "io.insert-koin:koin-ksp-compiler:$koin_annotations_version")
    add("kspAndroid", "io.insert-koin:koin-ksp-compiler:$koin_annotations_version")
    add("kspIosX64", "io.insert-koin:koin-ksp-compiler:$koin_annotations_version")
    add("kspIosArm64", "io.insert-koin:koin-ksp-compiler:$koin_annotations_version")
    add("kspIosSimulatorArm64", "io.insert-koin:koin-ksp-compiler:$koin_annotations_version")
}

// Trigger Common Metadata Generation from Native tasks
tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }.configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
}
```

:::info
For complete KMP setup and architecture patterns, see [Koin Annotations KMP](/docs/reference/koin-annotations/kmp).
:::

## Next Steps

Setup complete! Continue to:

- [Getting Started with Koin Annotations](/docs/reference/koin-annotations/start) - Learn how to use annotations in your code
- [Annotations Definitions](/docs/reference/koin-annotations/definitions) - Detailed annotation reference
- [Annotations Inventory](/docs/reference/koin-annotations/annotations-inventory) - Complete list of available annotations
