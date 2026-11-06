---
title: KSP Processor Setup (Deprecated)
description: Reference setup for koin-ksp-compiler, the deprecated KSP processor for Koin annotations, kept for existing projects until they move to the Compiler Plugin.
---

This page documents the Gradle setup of `koin-ksp-compiler`, the KSP processor that used to read Koin annotations. It is kept for projects that still use it. New projects use the [Koin Compiler Plugin](/docs/setup/compiler-plugin).

:::warning
`koin-ksp-compiler` is deprecated and will be removed in a future Koin version. The annotations are not deprecated: `koin-annotations` is part of the main Koin project and is processed by the Koin Compiler Plugin. Only the processor changes. See [Migrating from KSP to the Compiler Plugin](/docs/migration/from-ksp-to-compiler-plugin).
:::

## When to keep the KSP processor

Keep it only for a short time, if:

- **Kotlin version**: your project can't move to Kotlin 2.3.20 or later yet, which the Compiler Plugin requires.
- **Migration in progress**: you need to ship before you can switch the build.

Compared with the Compiler Plugin, the KSP processor generates visible source files, needs a KSP configuration per Kotlin Multiplatform target, and uses generated extensions such as `AppModule().module` to start Koin.

## Gradle setup

```kotlin
// build.gradle.kts
plugins {
    id("com.google.devtools.ksp") version "$ksp_version"
}

dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-annotations:$koin_ksp_version")
    ksp("io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
}
```

With KSP, `koin-annotations` and `koin-ksp-compiler` use the Koin Annotations version (`$koin_ksp_version`), not the Koin version. With the Compiler Plugin, `koin-annotations` follows the Koin version.

### Version compatibility

| Koin Annotations | KSP | Kotlin |
|------------------|-----|--------|
| 1.4 | 1.9 | 1.9 |
| 2.0 | 2.0 | 2.0 |
| 2.1, 2.2 | 2.1, 2.2 | 2.1, 2.2 |
| 2.3 | 2.3 | Independent |

## Starting Koin with generated modules

KSP generates a `.module` extension for each `@Module` class:

```kotlin
import org.koin.ksp.generated.*

@Single
class MyComponent

@Module
@ComponentScan
class MyModule

fun main() {
    startKoin {
        modules(MyModule().module)
    }
}
```

## Compile-time check

The KSP processor has its own configuration check, turned on with a KSP argument:

```kotlin
// build.gradle.kts
ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}
```

The Compiler Plugin replaces it with checks of the full graph and of every `get()` call site. See [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety).

## Kotlin Multiplatform

KSP needs one configuration per target:

```kotlin
// shared/build.gradle.kts
plugins {
    id("com.google.devtools.ksp")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.insert-koin:koin-core:$koin_version")
            implementation("io.insert-koin:koin-annotations:$koin_ksp_version")
        }
    }
}

dependencies {
    add("kspAndroid", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosX64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosArm64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosSimulatorArm64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
}
```

With the Compiler Plugin, you apply the plugin once and add `koin-annotations` to `commonMain`.

## Moving to the Compiler Plugin

The move keeps your annotated classes as they are. You remove KSP and `koin-ksp-compiler`, apply the Compiler Plugin, delete the generated sources, and replace `modules(MyModule().module)` with `startKoin<MyApp>()`. The step-by-step guide is [Migrating from KSP to the Compiler Plugin](/docs/migration/from-ksp-to-compiler-plugin).

If you get stuck, ask on the [Kotlin Slack #koin channel](https://kotlinlang.slack.com/messages/koin/) or open an issue on [InsertKoinIO/koin-annotations](https://github.com/InsertKoinIO/koin-annotations).

## Related

- [Migrating from KSP](/docs/migration/from-ksp-to-compiler-plugin): step-by-step migration.
- [Compiler Plugin Setup](/docs/setup/compiler-plugin): install the replacement.
- [Choosing your style](/docs/intro/choosing-your-style): annotations and the other styles.
- [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory): every annotation.
