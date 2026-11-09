---
title: Koin Compiler Plugin
description: What the Koin Compiler Plugin is, how it turns your DSL and annotations into wiring code, what it checks at build time, and what it needs.
---

The Koin Compiler Plugin is a Kotlin compiler plugin that runs inside the Kotlin compiler (K2). It writes the constructor wiring for your definitions and checks your dependency graph at build time. It powers both the Compiler Plugin DSL and annotations. This page explains what it does and how. To install it, see [Compiler Plugin Setup](/docs/setup/compiler-plugin). To compare it with the Constructor and Classic DSL, see [Choosing your style](/docs/intro/choosing-your-style).

## What it does to your code

You write the type, the plugin writes the constructor call:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class UserRepository(private val api: ApiService, private val cache: UserCache)

val appModule = module {
    single<UserRepository>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class UserRepository(private val api: ApiService, private val cache: UserCache)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

The Classic DSL does not need the plugin: you write the wiring yourself. The Constructor DSL (`singleOf(::T)`) doesn't need it either.

```kotlin
val appModule = module {
    single { UserRepository(get(), get()) }
}
```

If the plugin is applied, it still checks these `get()` calls and `singleOf(::T)` definitions at build time.

</TabItem>
</Tabs>

During compilation, the plugin replaces `single<UserRepository>()` (or the `@Singleton` class) with the equivalent of:

```kotlin
single { UserRepository(get(), get()) }
```

The generated code is the same runtime code the Classic DSL uses. Nothing changes at runtime: definitions go into the same container, and Koin resolves them the same way. Each parameter type is handled: nullable types use `getOrNull()`, `Lazy<T>` uses `inject()`, `List<T>` uses `getAll()`, and annotated parameters (`@Named`, `@InjectedParam`, `@Property`) use the matching call. The full table is in [Definitions](/docs/reference/koin-core/definitions#constructor-parameters).

In DSL style, the module says where a class is registered, and annotations on the class's parameters say how they are resolved:

```kotlin
class UserPresenter(
    @InjectedParam val userId: String,
    @Named("remote") val api: ApiService,
    val repository: UserRepository,
)

val appModule = module {
    factory<UserPresenter>()
}
```

## How it works

The plugin hooks into two phases of the Kotlin K2 compiler:

```text
Kotlin sources
     │
     ▼
  FIR (frontend)   finds @Module, @ComponentScan, @Configuration and declares
                   the generated module properties and cross-module hints
     │
     ▼
  IR (backend)     1. collects annotated definitions and fills the modules
                   2. rewrites DSL calls: single<T>() becomes single { T(get()) }
                   3. at entry points (startKoin, @KoinApplication), assembles
                      the full graph and validates it
                   4. validates get(), inject(), koinViewModel() call sites
     │
     ▼
  Bytecode (JVM, Android, Native, JS, Wasm)
```

No source file is generated in your project. To see across Gradle modules, the plugin adds small synthetic marker functions ("hints") to each compiled module. A downstream module reads them to find definitions declared in its dependencies. This is why every Gradle module that declares Koin definitions must apply the plugin.

## What it validates

The plugin validates the graph at each Koin entry point: `startKoin`, `koinApplication`, `@KoinApplication`, `KoinApplication.withConfiguration<T>()` and Ktor's `install(Koin) {}`. For each definition it checks that every non-nullable constructor parameter has a matching definition, with the right qualifier and in a reachable scope. It also checks every `get<T>()`, `inject<T>()` and `koinViewModel<T>()` call.

A missing dependency fails the build:

```text
[Koin][KOIN-D001] Missing dependency: ApiService
  required by: UserRepository (parameter 'api')
  in module: ...
```

The full list of checks, error codes and the `@Provided` escape hatch are in [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety).

## Limits

- **Runtime-loaded modules**: modules added later with `loadKoinModules()` are not part of the graph the plugin sees. Types they provide must be marked `@Provided`.
- **Library modules alone**: a Gradle module with no Koin entry point is not validated on its own. It is checked when an app module assembles it.
- **Incremental compilation**: like other IR compiler plugins, some changes (moving classes between scanned packages, changing qualifiers) can leave stale results. A clean build fixes them. See [Compiler Plugin Setup](/docs/setup/compiler-plugin) for details.
- **Kotlin versions**: the plugin uses compiler APIs that change between Kotlin releases. Each plugin release is verified on a list of Kotlin versions, and other versions print a warning.

## Requirements

- Kotlin 2.3.20 or later (K2 compiler). Plugin 1.2.1 is verified on Kotlin 2.3.20, 2.4.0, 2.4.10 and 2.4.20.
- Koin 4.2.0 or later.
- Gradle plugin `io.insert-koin.compiler.plugin`, applied in every Gradle module that declares definitions.

The Compiler Plugin DSL lives in the package `org.koin.plugin.module.dsl`, shipped with `koin-core`. The Classic DSL stays in `org.koin.dsl`, the Constructor DSL in `org.koin.core.module.dsl`.

The plugin replaces the deprecated KSP processor `koin-ksp-compiler`, with the same annotations. See [Migrating from KSP](/docs/migration/from-ksp-to-compiler-plugin). The source is on GitHub: [InsertKoinIO/koin-compiler-plugin](https://github.com/InsertKoinIO/koin-compiler-plugin).

## Related

- [Compiler Plugin Setup](/docs/setup/compiler-plugin): install and configure the plugin.
- [Choosing your style](/docs/intro/choosing-your-style): annotations or the DSL (Compiler Plugin, Constructor or Classic DSL).
- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): every check and error code.
- [Compiler Plugin Options](/docs/reference/koin-annotations/options): every Gradle option.
- [Definitions](/docs/reference/koin-core/definitions): how constructor parameters are resolved.
