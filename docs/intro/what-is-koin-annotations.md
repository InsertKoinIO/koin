---
title: What is Koin Annotations?
---

# What is Koin Annotations?

### Familiar Annotation Style — Part of the Main Koin Project

**Koin Annotations** is the annotation-based way to define your dependencies in Koin. If you prefer the style of `@Singleton`, `@Factory`, `@KoinViewModel` over a Kotlin DSL, this is for you.

It is **part of the main Koin project** — same GitHub repository, same release cycle, same Koin version, same maintainers. Not a side project, not a community fork, not a separate framework. It's processed by the **Koin Compiler Plugin** for compile-time safety, just like the DSL.

## In a Nutshell

```kotlin
@Singleton
class UserRepository(private val api: ApiService)

@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()

@Module
@ComponentScan("com.myapp")
class AppModule
```

That's the whole idea: annotate your classes, declare a module, the Koin Compiler Plugin wires the rest at build time.

## Part of the Main Koin Project

The `koin-annotations` library is **part of the main Koin project**. It lives in the same repository, ships under the **same Koin version** as `koin-core`, follows the same release cycle, and is covered by the Koin BOM:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-annotations") // same Koin version, same BOM
}
```

What this means in practice:

- **Not deprecated** — annotations are a first-class, fully supported style
- **Not a separate product** — there's no "Koin Annotations" project to track on its own
- **Versions stay in sync** — `koin-core` and `koin-annotations` always match
- **Full feature parity with the DSL** — anything you can do with the DSL, you can do with annotations

## Now Powered by the Koin Compiler Plugin

Koin Annotations is processed by the **Koin Compiler Plugin** — a native **Kotlin Compiler Plugin (K2)** that integrates directly with the Kotlin compiler. No KSP, no generated files to commit, no extra processing step.

What you get:

- **Auto-wiring** — constructor parameters are detected and resolved automatically
- **Compile-time safety** — missing dependencies, qualifier mismatches, and bad bindings are caught at build time
- **Simpler KMP setup** — no per-target KSP configuration
- **Same annotations** — `@Singleton`, `@Factory`, `@KoinViewModel`, `@Module`, `@ComponentScan`, `@Named`, `@InjectedParam`, etc.

See [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin) for the full picture of how it works and what it generates.

## `koin-ksp-compiler` is Deprecated

:::warning
The legacy KSP processor `koin-ksp-compiler` is **deprecated** and will be removed in a future Koin version.
:::

The annotations themselves are **not** deprecated — only the KSP-based processor that used to handle them. Migration is mechanical:

- **Same annotations** — your `@Singleton`, `@Module`, `@ComponentScan` code stays exactly the same
- **Drop the KSP plugin** — replace with the Koin Compiler Plugin
- **Delete generated files** — the Compiler Plugin doesn't produce visible generated sources

See [Migrating from KSP to Compiler Plugin](/docs/migration/from-ksp-to-compiler-plugin) for the step-by-step.

## When to Choose Annotations

Both annotations and the DSL are first-class. Pick annotations if:

- You're coming from Hilt, Dagger, or Spring and want a familiar style
- You prefer co-locating the definition with the class it describes
- Your team standardizes on annotation-based configuration

Pick the DSL if you prefer a Kotlin-native, code-only style. You can also **mix both** in the same project — they're processed by the same Compiler Plugin.

## Next Steps

- **[Koin Compiler Plugin](/docs/intro/koin-compiler-plugin)** — How the plugin powers your annotations
- **[Annotations Reference](/docs/reference/koin-annotations/start)** — Full annotation catalog and patterns
- **[Migrating from KSP to Compiler Plugin](/docs/migration/from-ksp-to-compiler-plugin)** — Upgrade path from `koin-ksp-compiler`
- **[What is Koin?](/docs/intro/what-is-koin)** — The bigger picture
