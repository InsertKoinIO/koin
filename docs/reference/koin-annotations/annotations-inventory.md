---
title: Annotations Reference
description: Every Koin annotation with its package, targets, parameters and an example, for use with the Koin Compiler Plugin.
---

This page lists every Koin annotation, with its package, where it can be placed, its parameters, and one example. Each entry links to the page that explains the concept. To set up annotations, see [Compiler Plugin Setup](/docs/setup/compiler-plugin). To compare annotations with the DSL, see [Choosing your style](/docs/intro/choosing-your-style).

The annotations come from the `io.insert-koin:koin-annotations` artifact (same version as Koin). The Koin Compiler Plugin reads them at build time and generates the definitions. Most live in `org.koin.core.annotation`, Android-only ones in `org.koin.android.annotation`.

## At a glance

| Annotation | Package | Declares |
|------------|---------|----------|
| [`@Singleton` / `@Single`](#singleton--single) | `org.koin.core.annotation` | A `single` definition |
| [`@Factory`](#factory) | `org.koin.core.annotation` | A `factory` definition |
| [`@Scoped`](#scoped) | `org.koin.core.annotation` | A `scoped` definition |
| [`@KoinViewModel`](#koinviewmodel) | `org.koin.core.annotation` | A `viewModel` definition |
| [`@KoinWorker`](#koinworker) | `org.koin.android.annotation` | A WorkManager `worker` definition |
| [`@Scope`](#scope) | `org.koin.core.annotation` | The scope a definition belongs to |
| [`@ViewModelScope`](#viewmodelscope) | `org.koin.core.annotation` | A definition in the ViewModel scope |
| [`@ActivityScope`](#activityscope) | `org.koin.android.annotation` | A definition in the Activity scope |
| [`@ActivityRetainedScope`](#activityretainedscope) | `org.koin.android.annotation` | A definition in the retained Activity scope |
| [`@FragmentScope`](#fragmentscope) | `org.koin.android.annotation` | A definition in the Fragment scope |
| [`@ScopeId`](#scopeid) | `org.koin.core.annotation` | A parameter resolved from a given scope |
| [`@Named`](#named) | `org.koin.core.annotation` | A string qualifier |
| [`@Qualifier`](#qualifier) | `org.koin.core.annotation` | A type or string qualifier |
| [`@InjectedParam`](#injectedparam) | `org.koin.core.annotation` | A parameter passed by the caller |
| [`@Property`](#property) | `org.koin.core.annotation` | A parameter read from a Koin property |
| [`@PropertyValue`](#propertyvalue) | `org.koin.core.annotation` | A default value for a property |
| [`@Provided`](#provided) | `org.koin.core.annotation` | A type provided outside the checked graph |
| [`@Module`](#module) | `org.koin.core.annotation` | A module class |
| [`@ComponentScan`](#componentscan) | `org.koin.core.annotation` | Packages to scan for definitions |
| [`@Configuration`](#configuration) | `org.koin.core.annotation` | Configuration labels for a module |
| [`@KoinApplication`](#koinapplication) | `org.koin.core.annotation` | The application entry point |
| [`@Monitor`](#monitor) | `org.koin.core.annotation` | Functions traced by the Kotzilla SDK |

## Definitions

A definition annotation goes on a class (Koin calls its constructor) or on a function (Koin calls the function, top-level or inside a `@Module` class). Constructor and function parameters are resolved from the container. How each parameter type is resolved: [Definitions](/docs/reference/koin-core/definitions#constructor-parameters).

When `binds` is empty, the definition is bound to its class and all its supertypes. List types in `binds` to bind only those.

### @Singleton / @Single

Target: `CLASS`, `FUNCTION`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `binds` | `Array<KClass<*>>` | `[]` | Types to bind. Empty binds all supertypes |
| `createdAtStart` | `Boolean` | `false` | Create the instance when Koin starts |

```kotlin
@Singleton(binds = [UserRepository::class])
class UserRepositoryImpl(private val api: ApiService) : UserRepository
```

`@Singleton` and `@Single` are equivalent; prefer `@Singleton`, the JSR-330 name. One instance is kept for the lifetime of the container. Concept: [Definitions](/docs/reference/koin-core/definitions).

### @Factory

Target: `CLASS`, `FUNCTION`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `binds` | `Array<KClass<*>>` | `[]` | Types to bind. Empty binds all supertypes |

```kotlin
@Factory
class UserPresenter(private val repository: UserRepository)
```

A new instance is created for each request. Concept: [Definitions](/docs/reference/koin-core/definitions).

### @Scoped

Target: `CLASS`, `FUNCTION`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `binds` | `Array<KClass<*>>` | `[]` | Types to bind. Empty binds all supertypes |

```kotlin
@Scope(UserSession::class)
@Scoped
class SessionCache(private val repository: UserRepository)
```

One instance per scope, released when the scope closes. Use it with `@Scope` or a scope archetype (`@ViewModelScope`, `@ActivityScope`, ...). Concept: [Scopes](/docs/reference/koin-core/scopes).

### @KoinViewModel

Target: `CLASS`, `FUNCTION`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `binds` | `Array<KClass<*>>` | `[]` | Types to bind. Empty binds all supertypes |

```kotlin
@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

Works on Android and Compose Multiplatform targets. Requires `io.insert-koin:koin-core-viewmodel` on the classpath (the build fails with `KOIN-A001` otherwise). Concept: [ViewModel](/docs/reference/koin-core/viewmodel).

:::note
With the Compiler Plugin, `@KoinViewModel` is in `org.koin.core.annotation`. The KSP processor used `org.koin.android.annotation.KoinViewModel`: update the import when you migrate (see [Migrating from KSP](/docs/migration/from-ksp-to-compiler-plugin)).
:::

### @KoinWorker

Package: `org.koin.android.annotation` (Android only). Target: `CLASS`, `FUNCTION`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `binds` | `Array<KClass<*>>` | `[]` | Types to bind |

```kotlin
@KoinWorker
class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: UserRepository,
) : CoroutineWorker(context, params)
```

Requires `io.insert-koin:koin-android-workmanager` (the build fails with `KOIN-A002` otherwise). Concept: [WorkManager](/docs/reference/koin-android/workmanager).

## Scopes

### @Scope

Target: `CLASS`, `FUNCTION`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `KClass<*>` | `Unit::class` | Scope type |
| `name` | `String` | `""` | Scope name, instead of a type |

```kotlin
@Scope(UserSession::class)
class SessionCache(private val repository: UserRepository)

@Scope(name = "checkout")
class CheckoutState
```

Puts the definition in the given scope. On its own, `@Scope` declares a `scoped` definition. Add `@Scoped`, `@Factory` or `@KoinViewModel` to choose another kind or to set `binds`. Concept: [Scopes](/docs/reference/koin-core/scopes).

### @ViewModelScope

Target: `CLASS`, `FUNCTION`. No parameters.

```kotlin
@ViewModelScope
class UserSessionCache(private val repository: UserRepository)
```

Declares a scoped definition in the scope of a ViewModel, created with the ViewModel and closed when it is cleared. Concept: [Scopes](/docs/reference/koin-core/scopes).

### @ActivityScope

Package: `org.koin.android.annotation` (Android only). Target: `CLASS`, `FUNCTION`. No parameters.

```kotlin
@ActivityScope
class ScreenTracker(private val analytics: Analytics)
```

Declares a scoped definition in the Activity scope. The Activity must provide that scope (see [Android Scopes](/docs/reference/koin-android/scope)).

### @ActivityRetainedScope

Package: `org.koin.android.annotation` (Android only). Target: `CLASS`, `FUNCTION`. No parameters.

```kotlin
@ActivityRetainedScope
class FormState
```

Declares a scoped definition in the retained Activity scope, which survives configuration changes. See [Android Scopes](/docs/reference/koin-android/scope).

### @FragmentScope

Package: `org.koin.android.annotation` (Android only). Target: `CLASS`, `FUNCTION`. No parameters.

```kotlin
@FragmentScope
class ListAdapterFactory
```

Declares a scoped definition in the Fragment scope. See [Android Scopes](/docs/reference/koin-android/scope).

### @ScopeId

Target: `VALUE_PARAMETER`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `KClass<*>` | `Unit::class` | Scope type. The scope ID is the class full name |
| `name` | `String` | `""` | Scope ID |

```kotlin
@Factory
class ProfileService(@ScopeId(name = "user_session") val session: UserSession)
```

Resolves the parameter from the scope with that ID instead of the current scope (generated as `getScope("user_session").get()`). Compile-time safety does not check it, since the scope exists only at runtime. Concept: [Scopes](/docs/reference/koin-core/scopes).

## Qualifiers

### @Named

Target: `CLASS`, `FUNCTION`, `VALUE_PARAMETER`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | String qualifier |
| `type` | `KClass<*>` | `Unit::class` | Type qualifier. Use `@Qualifier(Type::class)` instead with the Compiler Plugin |

```kotlin
@Singleton
@Named("local")
class LocalDatabase : Database

@Singleton
class UserRepository(@Named("local") private val database: Database)
```

The same string on the definition and on the parameter links them. `jakarta.inject.Named` and `javax.inject.Named` are read the same way. Concept: [Qualifiers](/docs/reference/koin-core/qualifiers).

### @Qualifier

Target: `CLASS`, `FUNCTION`, `VALUE_PARAMETER`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `KClass<*>` | `Unit::class` | Type qualifier |
| `name` | `String` | `""` | String qualifier |

```kotlin
@Singleton
@Qualifier(Remote::class)
class RemoteDataSource : DataSource

@Singleton
class UserRepository(@Qualifier(Remote::class) private val source: DataSource)
```

`@Qualifier` also marks your own qualifier annotations. Every annotation class annotated with `@Qualifier` or `@Named` (or `jakarta.inject.Qualifier`, `javax.inject.Qualifier`) becomes a qualifier:

```kotlin
@Qualifier
annotation class IoDispatcher

@Singleton
@IoDispatcher
fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
```

A custom qualifier with no argument is a type qualifier on the annotation class. With a string or enum argument (`@Dispatcher(AppDispatchers.IO)`), the argument value is the qualifier. Concept: [Qualifiers](/docs/reference/koin-core/qualifiers).

## Parameters

### @InjectedParam

Target: `VALUE_PARAMETER`. No parameters.

```kotlin
@Factory
class UserPresenter(@InjectedParam val userId: String, private val repository: UserRepository)

// caller
val presenter: UserPresenter = get { parametersOf("user-42") }
```

The value comes from `parametersOf(...)` at the call site, not from the container. Compile-time safety checks that call sites pass the right number and types of values (`KOIN-D005`, `KOIN-D006`). Concept: [Injected Parameters](/docs/reference/koin-core/injection-parameters).

### @Property

Target: `VALUE_PARAMETER`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | required | Property key |

```kotlin
@Factory
class ApiClient(@Property("api_url") val url: String)
```

Resolves the parameter with `getProperty("api_url")`. If a `@PropertyValue` with the same key exists, it is the fallback value. Without one, the build shows warning `KOIN-P001` and the property must be set at startup. Concept: [Properties](/docs/reference/koin-core/starting-koin#properties).

### @PropertyValue

Target: `FIELD`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | required | Property key |

```kotlin
@PropertyValue("api_url")
val defaultApiUrl = "https://api.example.com"

@Factory
class ApiClient(@Property("api_url") val url: String)
```

Declares the default value used when the property is not set (generated as `getProperty("api_url", defaultApiUrl)`).

### @Provided

Target: `CLASS`, `FUNCTION`, `VALUE_PARAMETER`. No parameters.

```kotlin
@Singleton
class AnalyticsService(@Provided val firebase: FirebaseAnalytics)
```

Tells compile-time safety that the type is available at runtime without a declaration it can see (platform object, SDK, module loaded later). On a class, every use of the type is skipped. On a parameter, only that parameter. Runtime resolution is unchanged. Common Android types (`Context`, `Activity`, `Fragment`, `SavedStateHandle`, `WorkerParameters`, ...) don't need it. Concept: [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety#external-types-provided).

## Modules and application

### @Module

Target: `CLASS`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `includes` | `Array<KClass<*>>` | `[]` | Other `@Module` classes to include |
| `createdAtStart` | `Boolean` | `false` | Create the module's `single` definitions when Koin starts |

```kotlin
@Module(includes = [NetworkModule::class])
class DataModule {

    @Singleton
    fun database(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app-db").build()
}
```

Groups definitions: annotated functions inside the class, plus annotated classes found by `@ComponentScan`. Concept: [Modules](/docs/reference/koin-core/modules).

### @ComponentScan

Target: `CLASS`, `FIELD`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `vararg String` | empty | Packages to scan. Empty scans the package of the annotated class |

```kotlin
@Module
@ComponentScan("com.myapp.data", "com.myapp.domain")
class AppModule
```

Adds to the module every annotated class and top-level function in the given packages and their subpackages, across Gradle modules. Concept: [Modules](/docs/reference/koin-core/modules).

:::note
The Compiler Plugin matches a package name and its subpackages. The glob patterns of the KSP processor (`com.example.**`, `com.*.service`) are not supported: list the packages instead.
:::

### @Configuration

Target: `CLASS`, `FIELD`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `vararg String` | empty | Configuration labels. Empty means `"default"` |

```kotlin
@Module
@ComponentScan
@Configuration
class CoreModule            // label "default"

@Module
@Configuration("default", "test")
class FakeDataModule        // labels "default" and "test"
```

A `@KoinApplication` loads every module that carries one of its configuration labels, including modules from other Gradle modules, without listing them. `@Configuration("default")` is the same as `@Configuration`. Concept: [Modules](/docs/reference/koin-core/modules).

### @KoinApplication

Target: `CLASS`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `configurations` | `Array<String>` | `[]` | Configuration labels to load. Empty loads `"default"` |
| `modules` | `Array<KClass<*>>` | `[Unit::class]` (none) | `@Module` classes to load in addition |

```kotlin
@KoinApplication(modules = [AppModule::class])
object MyApp

fun main() {
    startKoin<MyApp> {
        printLogger()
    }
}
```

Marks the entry point. Start it with `startKoin<MyApp>()`, `koinApplication<MyApp>()` or `koinConfiguration<MyApp>()` (package `org.koin.plugin.module.dsl`). This is where compile-time safety checks the full graph. Modules from `@Configuration` load first, then the `modules` list in its order, so the explicit list wins on overrides. Concept: [Starting Koin](/docs/reference/koin-core/starting-koin).

## Monitoring

### @Monitor

Target: `CLASS`, `FUNCTION`. No parameters.

```kotlin
@Monitor
class UserService(private val repository: UserRepository) {
    fun findUser(id: String): User? = repository.findById(id)
}
```

The Compiler Plugin wraps the function body (or every public function of the class) with a Kotzilla SDK trace. It requires the Kotzilla SDK (`io.kotzilla:kotzilla-core`) on the classpath. Without it, the build shows warning `KOIN-M001` and nothing is traced. Setup and data collected: [Monitor](/docs/tooling/monitor).

## JSR-330 annotations

The Compiler Plugin also reads `jakarta.inject` and `javax.inject` annotations: `@Singleton`, `@Named`, `@Inject` and `@Qualifier`. See [JSR-330](/docs/reference/koin-android/jsr330).

## Removed annotations

`@ExternalDefinition`, `@MetaDefinition`, `@MetaModule` and `@MetaApplication` (package `org.koin.meta.annotations`) were generated by the KSP processor for its own use. The Compiler Plugin does not use them, and you never write them by hand.

## Related

- [Definitions](/docs/reference/koin-core/definitions): definition types, binding, constructor parameters.
- [Modules](/docs/reference/koin-core/modules): `@Module`, `@ComponentScan` and `@Configuration` in practice.
- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): what the plugin checks from these annotations.
- [Compiler Plugin Options](/docs/reference/koin-annotations/options): Gradle options of the plugin.
- [DSL Reference](/docs/reference/koin-core/dsl): the DSL equivalent of each annotation.
