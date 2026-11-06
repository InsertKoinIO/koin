---
title: Choosing your style
description: Compare annotations and the three forms of the Koin DSL (Compiler Plugin, Constructor and Classic). Requirements, tradeoffs, a default for new projects, and how to mix them.
---

Koin offers two styles to declare definitions, both filling the same container: **annotations** on your classes, or the **Koin DSL** in `module { }` blocks. The DSL comes in three forms: the Compiler Plugin DSL (`single<T>()`), the Constructor DSL (`singleOf(::T)`) and the Classic DSL (`single { T(get()) }`). The choice changes how you write modules, not how Koin works at runtime. This page shows each style, its requirements and tradeoffs, and recommends a default. It assumes you know what a definition and a module are (see [Core Concepts](/docs/intro/core-concepts)).

## The styles side by side

The same module in each style:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val appModule = module {
    single<ApiService>()
    single<UserRepositoryImpl>() bind UserRepository::class
    viewModel<UserViewModel>()
}
```

You name the type. The [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin) reads its constructor and generates the wiring.

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class ApiService

@Singleton
class UserRepositoryImpl(private val api: ApiService) : UserRepository

@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()

@Module
@ComponentScan("com.example.app")
class AppModule
```

You annotate each class. `@ComponentScan` gathers them into a module, and the Compiler Plugin generates the wiring. Interfaces are bound automatically.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

Constructor DSL, with constructor references:

```kotlin
import org.koin.dsl.module
import org.koin.core.module.dsl.*

val appModule = module {
    singleOf(::ApiService)
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    viewModelOf(::UserViewModel)
}
```

Classic DSL, with the constructor call written by hand:

```kotlin
val appModule = module {
    single { ApiService() }
    single<UserRepository> { UserRepositoryImpl(get()) }
    viewModel { UserViewModel(get()) }
}
```

Both forms work without the compiler plugin. When the plugin is applied, they are checked at build time too.

</TabItem>
</Tabs>

## Requirements

| | Compiler Plugin DSL | Annotations | Constructor / Classic DSL |
|---|---|---|---|
| Koin version | 4.2.0+ | 4.2.0+ | Any |
| Kotlin version | 2.3.20+ (K2) | 2.3.20+ (K2) | Any version supported by your Koin version |
| Gradle plugin | `io.insert-koin.compiler.plugin` (required) | `io.insert-koin.compiler.plugin` (required) | Optional: adds compile-time checks |
| Extra dependency | `koin-annotations` only for parameter annotations (`@Named`, `@InjectedParam`) | `koin-annotations` | None |
| Imports | `org.koin.plugin.module.dsl.*` | `org.koin.core.annotation.*` | `org.koin.core.module.dsl.*` (Constructor), `org.koin.dsl.*` (Classic) |

Each Compiler Plugin release is verified on a list of Kotlin versions. Plugin 1.2.1 is verified on Kotlin 2.3.20, 2.4.0, 2.4.10 and 2.4.20. Installation steps are in [Compiler Plugin Setup](/docs/setup/compiler-plugin).

:::warning
The Compiler Plugin DSL functions (`single<T>()`, `factory<T>()`, `create(::T)`) are stubs. Without the Gradle plugin applied, the code compiles but throws `NotImplementedError` at runtime. Apply the plugin in every Gradle module that uses them.
:::

## Tradeoffs

### Compiler Plugin DSL

- **Compile-time checks**: the full graph and every `get()` call site are validated at build time.
- **No wiring code**: no `get()`, no constructor references. Adding a constructor parameter needs no module change.
- **Explicit modules**: you see every definition of a module in one place, and you can use Kotlin code (conditions, loops) around them.
- **Parameter annotations**: qualifiers and injected parameters are declared on the class (`@Named`, `@InjectedParam`), so the class depends on `koin-annotations`.
- **Kotlin coupling**: you need a recent K2 compiler, and a new Kotlin release may need a new plugin release.

### Annotations

- **Compile-time checks**: same as the Compiler Plugin DSL.
- **Definition next to the class**: the lifetime and bindings are visible where the class is written. Teams coming from Hilt, Dagger or Spring find the model familiar.
- **Automatic binding**: a class is bound to all its supertypes unless you list `binds`.
- **Scanning configuration**: definitions are found by package. You need `@Module` and `@ComponentScan` with the right packages, and `@Configuration` or `@KoinApplication` to assemble modules across Gradle modules. A class outside the scanned packages is not part of the module. The compile-time check reports it only if another definition needs it.
- **Gaps**: some options exist only in the DSL, such as the `onClose` callback. You write those definitions in a DSL module.
- **Kotlin coupling**: same as the Compiler Plugin DSL.

### Constructor DSL and Classic DSL

- **Works everywhere**: the compiler plugin is optional, no Kotlin version constraint beyond Koin's own.
- **Compile-time checks with the plugin**: since Compiler Plugin 1.2.0, applying the plugin checks `singleOf(::T)`, `factoryOf(::T)`, `scopedOf(::T)` and `viewModelOf(::T)` like `single<T>()`, and hand-written lambdas through their `get()` calls. Existing modules get compile-time checks without a rewrite.
- **Full control (Classic DSL)**: the lambda is plain Kotlin, so you can call builders, read configuration or choose an implementation at runtime.
- **Runtime errors without the plugin**: a missing definition is found when the code runs. Use [`verify()`](/docs/reference/koin-test/verify) in a unit test to catch it earlier.
- **Wiring to maintain**: `single { UserRepository(get(), get()) }` must follow constructor changes by hand. `singleOf(::UserRepository)` follows them, but needs explicit calls for nullable, lazy or qualified parameters.

## Which one to pick

Pick one style for the project and state it: annotations or the DSL.

For a new project on Kotlin 2.3.20 or later, use the **Compiler Plugin DSL**. It gives compile-time checks, keeps DI out of your classes and keeps modules as explicit Kotlin code. If your team prefers to declare definitions on the class, as with Hilt or Dagger, use **annotations**: they get the same checks and are JSR-330 compatible (`@Inject`, `@Singleton`, `@Named`).

Keep the **Constructor or Classic DSL** if you can't use a recent Kotlin version, if you build a library that must not require a compiler plugin from its users, or if your existing modules work and you don't want to change them. Apply the Compiler Plugin to get compile-time checks on them without a rewrite. Use a Classic DSL lambda wherever a definition needs custom logic.

## Mixing styles

All styles produce the same definitions, so one project can use several. Prefer one style per project. If you mix annotations and the DSL, keep one style per module. Common cases:

- **Compiler Plugin DSL and Classic DSL in one module**: write `single<T>()` for most definitions, and a lambda where you need custom logic. Both are DSL forms, so this is not mixing styles.
- **Annotations with a few DSL modules**: start from your `@KoinApplication` class and add DSL modules in the configuration block.
- **DSL start with annotated modules**: load a `@Module` class into a regular `startKoin`.

```kotlin
import org.koin.plugin.module.dsl.module
import org.koin.plugin.module.dsl.startKoin

// annotated application, plus DSL modules
startKoin<MyApp> {
    modules(legacyModule)
}

// DSL application, plus an annotated module
startKoin {
    modules(appModule)
    module<NetworkModule>()
}
```

`@Module(includes = [...])` and `@KoinApplication(modules = [...])` only accept `@Module` classes, not DSL `module { }` values. See [Modules](/docs/reference/koin-core/modules) and [Starting Koin](/docs/reference/koin-core/starting-koin) for the full rules.

## The KSP processor is deprecated

Before the Compiler Plugin, annotations were processed by `koin-ksp-compiler`, a KSP processor. That processor is deprecated and will be removed in a future Koin version. The annotations themselves are not deprecated: `koin-annotations` is part of the main Koin project and ships with each Koin version. If you use KSP today, your annotated classes stay the same and only the build setup and the start code change. See [Migrating from KSP to the Compiler Plugin](/docs/migration/from-ksp-to-compiler-plugin).

## Tabs on every page

Code samples in these docs use the same three tabs: Compiler Plugin DSL, Annotations, Constructor / Classic DSL. When you select a tab, the choice is saved in your browser and applied on every page. Samples that are the same in all styles, such as `by inject()` in an Activity, are shown once without tabs.

## Related

- [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin): how the plugin works and what it validates.
- [Compiler Plugin Setup](/docs/setup/compiler-plugin): install the Gradle plugin.
- [Definitions](/docs/reference/koin-core/definitions): every definition type, in every style.
- [DSL Reference](/docs/reference/koin-core/dsl) and [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory): every keyword and annotation.
- [Migrating from KSP](/docs/migration/from-ksp-to-compiler-plugin): move from `koin-ksp-compiler`.
