---
title: Setup
description: What to install for each platform, the recommended setup with the Koin BOM and the Compiler Plugin, and current versions.
---

This section explains what to add to your Gradle build to use Koin. Pick your platform below, then follow the linked section of [Gradle Setup](/docs/setup/gradle). If you have not chosen a declaration style yet, read [Choosing your style](/docs/intro/choosing-your-style) first: it decides whether you also need the Compiler Plugin.

## What to install

| Platform | Artifacts | Setup |
|----------|-----------|-------|
| Kotlin/JVM | `koin-core` | [Kotlin/JVM](/docs/setup/gradle#kotlin) |
| Android | `koin-android` | [Android](/docs/setup/gradle#android) |
| Android with Jetpack Compose | `koin-android`, `koin-compose`, `koin-compose-viewmodel` | [Jetpack Compose](/docs/setup/gradle#compose-android) |
| Compose Multiplatform | `koin-compose`, `koin-compose-viewmodel` | [Compose Multiplatform](/docs/setup/gradle#compose) |
| Kotlin Multiplatform | `koin-core` in `commonMain` | [Kotlin Multiplatform](/docs/setup/gradle#kotlin-multiplatform) |
| Ktor | `koin-ktor`, `koin-logger-slf4j` | [Ktor](/docs/setup/gradle#ktor) |
| Tests | `koin-test`, `koin-test-junit4` or `koin-test-junit5` | [Testing](/docs/setup/gradle#testing) |

## Recommended setup

For a new project:

1. **Koin BOM**: import `io.insert-koin:koin-bom` and declare Koin artifacts without versions, so they always match. See [Gradle Setup](/docs/setup/gradle).
2. **Koin Compiler Plugin**: apply the Gradle plugin `io.insert-koin.compiler.plugin` to get the Compiler Plugin DSL or annotations, with compile-time checks. It needs Kotlin 2.3.20 or later. See [Compiler Plugin Setup](/docs/setup/compiler-plugin).

If you use the Constructor or Classic DSL only, step 1 is enough. Step 2 is still recommended: it adds compile-time checks to those definitions.

:::warning
The KSP processor `koin-ksp-compiler` is deprecated. Its setup is kept in [KSP Processor Setup](/docs/setup/annotations-ksp) for existing projects only. New projects use the Compiler Plugin.
:::

## Versions

| Component | Current version | Kotlin |
|-----------|-----------------|----------|
| Koin (all libraries, including `koin-annotations`) | 4.2.2 | Built with 2.3.20 |
| Koin Compiler Plugin | 1.2.1 | Verified on 2.3.20, 2.4.0, 2.4.10, 2.4.20 (needs Koin 4.2.0+) |

Latest published versions:

- Koin: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-core?label=latest)](https://mvnrepository.com/artifact/io.insert-koin/koin-core)
- Koin Compiler Plugin: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-compiler-plugin?label=latest)](https://mvnrepository.com/artifact/io.insert-koin/koin-compiler-plugin)

Older Koin lines:

| Koin | Kotlin | Compiler Plugin |
|------|--------|-----------------|
| 4.2.x | 2.3.20+ | Yes |
| 4.1.x | 2.1+ | No, KSP processor only |
| 4.0.x | 1.9+ | No, KSP processor only |
| 3.5.x | 1.8+ | No |

What changed between versions and how to upgrade is in [Releases](/docs/support/releases).

## Related

- [Gradle Setup](/docs/setup/gradle): the BOM and every Koin artifact.
- [Compiler Plugin Setup](/docs/setup/compiler-plugin): install and configure the plugin.
- [Choosing your style](/docs/intro/choosing-your-style): which style needs what.
- [Tutorials](/docs/quickstart/kotlin): a first app on each platform.
- [Releases](/docs/support/releases): versions and upgrade notes.
