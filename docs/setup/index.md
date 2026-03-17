---
title: Setup & Versions
---

# Setting Up Koin

This guide covers everything you need to add Koin to your project.

## Quick Setup

Choose your platform to get started:

| Platform | Package | Guide |
|----------|---------|-------|
| **Kotlin/JVM** | `koin-core` | [Gradle Setup](/docs/setup/gradle#kotlin) |
| **Android** | `koin-android` | [Gradle Setup](/docs/setup/gradle#android) |
| **Android + Jetpack Compose** | `koin-android` + `koin-compose` | [Gradle Setup](/docs/setup/gradle#compose-android) |
| **Compose Multiplatform** | `koin-compose` | [Gradle Setup](/docs/setup/gradle#compose) |
| **Kotlin Multiplatform** | `koin-core` | [Gradle Setup](/docs/setup/gradle#kotlin-multiplatform) |
| **Ktor** | `koin-ktor` | [Gradle Setup](/docs/setup/gradle#ktor) |

## Recommended Setup: BOM + Compiler Plugin

For the best experience, we recommend:

1. **Use the Koin BOM** - Manages all Koin library versions
2. **Use the Koin Compiler Plugin** - Provides compile-time safety

See the **[Compiler Plugin Setup Guide](/docs/setup/compiler-plugin)** for detailed instructions.

## Setup Guides

### [Gradle Setup](/docs/setup/gradle)

Complete dependency configuration for all platforms:
- Koin BOM (recommended)
- Version catalogs
- Platform-specific packages
- Testing dependencies

### [Compiler Plugin Setup](/docs/setup/compiler-plugin)

Detailed guide for the Koin Compiler Plugin:
- Gradle plugin configuration
- Configuration options
- Kotlin version requirements
- Troubleshooting

### [KSP Annotations Setup](/docs/setup/annotations-ksp) (Deprecated)

Legacy setup for KSP-based annotations:
- ⚠️ Deprecated - migrate to Compiler Plugin
- Migration guide included

## Version Compatibility

| Koin Version | Kotlin Version | Compiler Plugin |
|--------------|----------------|-----------------|
| 4.2.x | 2.3+ | ✅ Recommended |
| 4.1.x | 2.1/2.2+ | ⚠️ KSP only |
| 4.0.x | 1.9/2.0+ | ⚠️ KSP only |
| 3.5.x | 1.8+ | ❌ Not available |

## Current Version

- **Koin**: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-core?label=latest)](https://mvnrepository.com/artifact/io.insert-koin/koin-core)
- **Koin Compiler Plugin**: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-compiler-plugin?label=latest)](https://mvnrepository.com/artifact/io.insert-koin/koin-compiler-plugin)

Find all Koin packages on [Maven Central](https://central.sonatype.com/search?q=io.insert-koin+koin-core&sort=name).

## Next Steps

After setup:
- **[Core Concepts](/docs/reference/koin-core/starting-koin)** - Learn how to use Koin
- **[Tutorials](/docs/quickstart/kotlin)** - Build your first app
- **[Android Integration](/docs/reference/koin-android/start)** - Android-specific features
