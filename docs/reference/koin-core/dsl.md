---
title: DSL Reference
description: Every Koin DSL keyword in one place, for the application, modules, definitions, options and scopes, in the Compiler Plugin, Constructor and Classic DSL.
---

This page lists every Koin DSL keyword in tables, with the package to import and a link to the page that explains it. It covers the three DSL forms: the Compiler Plugin DSL, the Constructor DSL (constructor references) and the Classic DSL (lambdas). For annotations, see the [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory).

## DSL styles

| Style | Example | Import |
|-------|---------|--------|
| Compiler Plugin DSL | `single<UserRepository>()` | `org.koin.plugin.module.dsl.*` |
| Constructor DSL | `singleOf(::UserRepository)` | `org.koin.core.module.dsl.*` |
| Classic DSL | `single { UserRepository(get()) }` | `org.koin.dsl.*` |

The Compiler Plugin DSL needs the [Koin Compiler Plugin](/docs/setup/compiler-plugin). The plugin reads the constructor, generates the `get()` calls and checks the graph at build time. Without the plugin, a call such as `single<T>()` throws at runtime: `Error while using 'single<T>()'. The Koin Compiler plugin is missing. Please fix your setup to use 'io.insert-koin.compiler.plugin' Koin Gradle Plugin.`

The constructor-reference form (`singleOf(::T)`) is also checked by the Compiler Plugin when it is applied. A hand-written lambda is not: the plugin tracks the type it provides, not what the lambda body does. To pick a style, see [Choosing your style](/docs/intro/choosing-your-style).

## Application DSL

Functions that create and start a Koin container. See [Starting Koin](/docs/reference/koin-core/starting-koin).

| Function | Package | What it does |
|----------|---------|--------------|
| `startKoin { }` | `org.koin.core.context` | Creates a `KoinApplication` and registers it in the global context. Throws `KoinApplicationAlreadyStartedException` if one is already started |
| `startKoin<T>()` | `org.koin.plugin.module.dsl` | Compiler Plugin: starts Koin with the modules of the `@KoinApplication` class `T` |
| `koinApplication { }` | `org.koin.dsl` | Creates a standalone `KoinApplication`, not registered globally. See [Context Isolation](/docs/reference/koin-core/context-isolation) |
| `koinApplication<T>()` | `org.koin.plugin.module.dsl` | Compiler Plugin: standalone application with the modules of `T` |
| `koinConfiguration { }` | `org.koin.dsl` | Declares a reusable `KoinConfiguration` without starting Koin |
| `koinConfiguration<T>()` | `org.koin.plugin.module.dsl` | Compiler Plugin: configuration with the modules of `T` |
| `stopKoin()` | `org.koin.core.context` | Closes the global container and all its instances |
| `loadKoinModules(module)` | `org.koin.core.context` | Loads modules into the started global container |
| `unloadKoinModules(module)` | `org.koin.core.context` | Unloads modules and drops their instances |

### Inside `startKoin { }` and `koinApplication { }`

| Function | What it does |
|----------|--------------|
| `modules(...)` | Loads modules: one `Module`, a vararg or a `List<Module>` |
| `modules(UserModule::class, ...)` | Compiler Plugin: loads the modules generated from `@Module` classes |
| `module<UserModule>()` | Compiler Plugin: loads the module generated from one `@Module` class |
| `withConfiguration<T>()` | Compiler Plugin: adds the modules of `@KoinApplication` class `T` to an existing application |
| `includes(configuration)` | Applies one or more `KoinConfiguration` or `KoinAppDeclaration` blocks |
| `printLogger(level)` | Logs to standard output. Default level `Level.INFO` |
| `logger(logger)` | Uses your own `Logger` implementation. The default is `EmptyLogger` (no output) |
| `properties(map)` | Loads properties from a `Map<String, Any>` |
| `fileProperties(fileName)` | JVM only. Loads properties from a file, default `/koin.properties` |
| `environmentProperties()` | JVM only. Loads system properties and environment variables |
| `allowOverride(Boolean)` | Allows or forbids definition override. Default `true`. See [Definitions](/docs/reference/koin-core/definitions#overriding-a-definition) |
| `createEagerInstances()` | Creates `createdAtStart` definitions. `startKoin` and `koinApplication` already call it |
| `options(...)` | Sets Koin options, such as `viewModelScopeFactory()`. See [ViewModel](/docs/reference/koin-core/viewmodel) |
| `lazyModules(...)` | Loads lazy modules in the background (`koin-core-coroutines`). See [Lazy Modules](/docs/reference/koin-core/lazy-modules) |

`modules(A::class)`, `module<T>()` and `withConfiguration<T>()` go in a plain `startKoin { }` or `koinApplication { }`, not inside `startKoin<T> { }`.

Platform extensions add more functions to this block:

| Function | Artifact | See |
|----------|----------|-----|
| `androidContext(context)`, `androidLogger(level)`, `androidFileProperties()` | `koin-android` | [Android Start](/docs/reference/koin-android/start) |
| `fragmentFactory()` | `koin-android` | [Fragment Factory](/docs/reference/koin-android/fragment-factory) |
| `workManagerFactory()` | `koin-androidx-workmanager` | [WorkManager](/docs/reference/koin-android/workmanager) |
| `slf4jLogger(level)` | `koin-logger-slf4j` | [Ktor](/docs/reference/koin-ktor/ktor) |

## Module DSL

See [Modules](/docs/reference/koin-core/modules) and [Lazy Modules](/docs/reference/koin-core/lazy-modules).

| Function | Package | What it does |
|----------|---------|--------------|
| `module { }` | `org.koin.dsl` | Declares a module |
| `module(createdAtStart = true) { }` | `org.koin.dsl` | Every `single` of the module is created at start |
| `lazyModule { }` | `org.koin.dsl` | Declares a module loaded later, in the background (`koin-core-coroutines`) |
| `includes(otherModule, ...)` | `org.koin.dsl` | Includes other modules, as vararg or `Collection<Module>` |
| `moduleA + moduleB` | `org.koin.dsl` | Builds a `List<Module>` |

## Definitions

What each keyword creates is explained in [Definitions](/docs/reference/koin-core/definitions).

| Definition | Compiler Plugin DSL | Constructor DSL | Classic DSL |
|------------|---------------------|-----------------|-------------|
| Single | `single<T>()` | `singleOf(::T)` | `single { T(get()) }` |
| Factory | `factory<T>()` | `factoryOf(::T)` | `factory { T(get()) }` |
| Scoped (in a scope block) | `scoped<T>()` | `scopedOf(::T)` | `scoped { T(get()) }` |
| ViewModel | `viewModel<T>()` | `viewModelOf(::T)` | `viewModel { T(get()) }` |
| Worker (Android) | `worker<T>()` | `workerOf(::T)` | `worker { T(get(), get()) }` |
| Fragment (Android) | Not available | `fragmentOf(::T)` | `fragment { T(get()) }` |
| Function or builder | `single { create(::buildT) }` | `singleOf(::buildT)` | `single { buildT(get()) }` |

Packages:

| Keywords | Compiler Plugin DSL | Constructor / Classic DSL |
|----------|---------------------|---------------------------|
| `single`, `factory`, `scoped`, `create` | `org.koin.plugin.module.dsl` | `org.koin.dsl` (lambda), `org.koin.core.module.dsl` (`singleOf`, `factoryOf`, `scopedOf`, `new`) |
| `viewModel`, `viewModelOf` (artifact `koin-core-viewmodel`) | `org.koin.plugin.module.dsl` | `org.koin.core.module.dsl` |
| `worker`, `workerOf` (artifact `koin-androidx-workmanager`) | `org.koin.plugin.module.dsl` | `org.koin.androidx.workmanager.dsl` |
| `fragment`, `fragmentOf` (artifact `koin-android`) | | `org.koin.androidx.fragment.dsl` |

`viewModel` and `viewModelOf` in `org.koin.androidx.viewmodel.dsl` and `org.koin.compose.viewmodel.dsl` are deprecated: import them from `org.koin.core.module.dsl`.

`create(::buildT)` must be the only instruction in its lambda. Otherwise the Compiler Plugin reports `create(::buildT) must be the only instruction in the lambda.` See [Definitions](/docs/reference/koin-core/definitions#classes-you-dont-own).

### Lambda parameters

The lambda definitions have this signature:

| Keyword | Parameters |
|---------|------------|
| `single(qualifier, createdAtStart) { }` | `qualifier: Qualifier? = null`, `createdAtStart: Boolean = false` |
| `factory(qualifier) { }` | `qualifier: Qualifier? = null` |
| `scoped(qualifier) { }` | `qualifier: Qualifier? = null` |
| `viewModel(qualifier) { }` | `qualifier: Qualifier? = null` |
| `worker(qualifier) { }` | `qualifier: Qualifier = named<T>()` |

### Inside a definition lambda

The lambda runs with a `Scope` receiver and a `ParametersHolder` argument (`params`):

| Function | Returns | See |
|----------|---------|-----|
| `get<T>()`, `get<T>(qualifier)` | The instance. Throws `NoDefinitionFoundException` if missing | [Injection](/docs/reference/koin-core/injection) |
| `getOrNull<T>()` | The instance or `null` | [Injection](/docs/reference/koin-core/injection) |
| `inject<T>()` | `Lazy<T>` | [Injection](/docs/reference/koin-core/injection) |
| `getAll<T>()` | All instances bound to `T` | [Advanced Patterns](/docs/reference/koin-core/advanced-patterns#collections-of-dependencies) |
| `getProperty(key)`, `getProperty(key, default)`, `getPropertyOrNull(key)` | A Koin property | [Starting Koin](/docs/reference/koin-core/starting-koin) |
| `params.get<T>()`, `(id) -> ...` destructuring | An injected parameter | [Injected Parameters](/docs/reference/koin-core/injection-parameters) |
| `new(::T)` | A new `T` with every constructor parameter resolved by `get()` | |
| `create(::T)` | Compiler Plugin: the same, with build-time checks | [Definitions](/docs/reference/koin-core/definitions#classes-you-dont-own) |
| `getSource<T>()` | The scope source object | [Scopes](/docs/reference/koin-core/scopes) |
| `androidContext()`, `androidApplication()` | The Android `Context` or `Application` | [Android Start](/docs/reference/koin-android/start) |

## Definition options

Options apply after a definition, with an infix operator or in a `withOptions { }` block. They work on every definition keyword.

| Option | Form | What it does | See |
|--------|------|--------------|-----|
| `bind` | `single<T>() bind I::class` | Adds a secondary type | [Definitions](/docs/reference/koin-core/definitions#interface-binding) |
| `binds` | `single<T>() binds arrayOf(I::class, J::class)` | Adds several secondary types | [Definitions](/docs/reference/koin-core/definitions#interface-binding) |
| `onClose` | `single<T>() onClose { it?.close() }` | Callback when the instance is released | [Definitions](/docs/reference/koin-core/definitions#release-callback) |
| `withOptions { }` | `single<T>() withOptions { ... }` | Opens the option block below | |
| `.override()` | `single<T> { ... }.override()` | Marks an allowed override when `allowOverride(false)` is set | [Definitions](/docs/reference/koin-core/definitions#overriding-a-definition) |

In `withOptions { }`, or in the trailing block of `singleOf(::T) { }`, `factoryOf`, `scopedOf` and `viewModelOf`:

| Option | What it does |
|--------|--------------|
| `named("name")` | String qualifier. See [Qualifiers](/docs/reference/koin-core/qualifiers) |
| `named<Type>()` | Type qualifier |
| `bind<I>()` | Adds a secondary type |
| `binds(listOf(I::class, J::class))` | Adds several secondary types |
| `createdAtStart()` | Creates the `single` when Koin starts |
| `onClose { }` | Release callback |

```kotlin
val appModule = module {
    single<UserRepositoryImpl>() withOptions {
        named("local")
        bind<UserRepository>()
        createdAtStart()
    }

    // Constructor DSL
    singleOf(::UserRepositoryImpl) {
        named("local")
        bind<UserRepository>()
    }
}
```

## Scope DSL

A scope block groups the definitions that live as long as a scope. See [Scopes](/docs/reference/koin-core/scopes).

| Function | Package | What it does |
|----------|---------|--------------|
| `scope<T> { }` | `Module` member | Scope identified by the type `T`, usually the type of the scope source |
| `scope(named("session")) { }` | `Module` member | Scope identified by a qualifier |

Inside a scope block:

| Definition | Compiler Plugin DSL | Constructor DSL | Classic DSL |
|------------|---------------------|-----------------|-------------|
| Scoped | `scoped<T>()` | `scopedOf(::T)` | `scoped { T(get()) }` |
| Factory | `factory<T>()` | `factoryOf(::T)` | `factory { T(get()) }` |
| ViewModel | `viewModel<T>()` | `viewModelOf(::T)` | `viewModel { T(get()) }` |
| Worker (Android) | `worker<T>()` | `workerOf(::T)` | `worker { T(get(), get()) }` |

```kotlin
val sessionModule = module {
    scope<UserSession> {
        scoped<SessionCache>()
        factory<SessionPresenter>()
    }
}
```

### Scope archetypes

An archetype is a scope block for a type of scope source rather than one class. Every source of that kind gets the definitions.

| Function | Artifact | Scope of | See |
|----------|----------|----------|-----|
| `viewModelScope { }` | `koin-core-viewmodel` | Each ViewModel | [ViewModel](/docs/reference/koin-core/viewmodel) |
| `activityScope { }` | `koin-android` | Each Activity | [Android Scopes](/docs/reference/koin-android/scope) |
| `activityRetainedScope { }` | `koin-android` | Each Activity, kept across configuration changes | [Android Scopes](/docs/reference/koin-android/scope) |
| `fragmentScope { }` | `koin-android` | Each Fragment | [Android Scopes](/docs/reference/koin-android/scope) |
| `navigationScope { }` | `koin-compose-viewmodel-navigation` | Each navigation destination (`NavBackStackEntry`) | [Compose Scopes](/docs/reference/koin-compose/compose-scopes) |
| `requestScope { }` | `koin-ktor` | Each Ktor call | [Ktor Scopes](/docs/reference/koin-ktor/ktor-scopes) |

## Related

- [Definitions](/docs/reference/koin-core/definitions): what each definition type does and when to use it.
- [Modules](/docs/reference/koin-core/modules): organize and compose modules.
- [Scopes](/docs/reference/koin-core/scopes): scope lifecycle, linking and closing.
- [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory): the annotation equivalent of this page.
- [Compiler Plugin Setup](/docs/setup/compiler-plugin): install the plugin that the Compiler Plugin DSL needs.
