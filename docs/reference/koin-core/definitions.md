---
title: Definitions
description: Declare how Koin creates your classes with single, factory, scoped and viewModel, bind them to interfaces, and wrap external libraries.
---

A definition tells Koin how to create one type and how long to keep the instance. This page covers the definition types, interface binding, how constructor parameters are resolved, and definitions for classes you don't own. It assumes you know what a module is (see [Core Concepts](/docs/intro/core-concepts)).

Code samples come in three tabs: Compiler Plugin DSL, Annotations, Constructor / Classic DSL. Pick yours, the choice applies to every page. If you are not sure which style to use, read [Choosing your style](/docs/intro/choosing-your-style).

## Definition types

| Type | Instance | Typical use |
|------|----------|-------------|
| `single` | One instance, kept for the lifetime of the Koin container | Repositories, API clients, databases |
| `factory` | A new instance each time it is requested | Presenters, use cases, short-lived objects |
| `scoped` | One instance per scope, released when the scope closes | Objects bound to an Activity, a session, a request |
| `viewModel` | Managed by the ViewModel lifecycle | ViewModels (Android and Compose Multiplatform) |
| `worker` | Created by WorkManager | Android `ListenableWorker` |

Declaring them:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val appModule = module {
    single<Database>()
    single<UserRepository>()
    factory<UserPresenter>()
    viewModel<UserViewModel>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class Database

@Singleton
class UserRepository(private val database: Database)

@Factory
class UserPresenter(private val repository: UserRepository)

@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

`@Singleton` and `@Single` are equivalent. Prefer `@Singleton`, the JSR-330 name.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.dsl.module
import org.koin.core.module.dsl.*

val appModule = module {
    singleOf(::Database)
    singleOf(::UserRepository)
    factoryOf(::UserPresenter)
    viewModelOf(::UserViewModel)
}
```

You can also write the constructor call yourself, with `get()` for each dependency:

```kotlin
val appModule = module {
    single { Database() }
    single { UserRepository(get()) }
    factory { UserPresenter(get()) }
    viewModel { UserViewModel(get()) }
}
```

</TabItem>
</Tabs>

With the Compiler Plugin DSL and annotations, you never write `get()`. The [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin) reads the constructor and generates the calls for you, then checks at build time that every dependency is declared.

`single` and `factory` are explained above. `scoped` needs a scope declaration: see [Scopes](/docs/reference/koin-core/scopes). `viewModel` has its own page: see [ViewModel](/docs/reference/koin-core/viewmodel). `worker` is covered in [WorkManager](/docs/reference/koin-android/workmanager).

### Single or factory?

Use `single` when the object has no per-caller state and is costly to create (a database, an HTTP client). Use `factory` when each caller needs its own instance, or the object holds state that must not be shared. If an instance should live longer than a factory but shorter than the app, you need a [scope](/docs/reference/koin-core/scopes).

## Interface binding

Most of the time, consumers depend on an interface, not on the implementation. Bind the implementation to the interface:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val dataModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class

    // several types at once
    single<MyServiceImpl>() binds arrayOf(ServiceA::class, ServiceB::class)
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class UserRepositoryImpl(private val database: Database) : UserRepository
```

Binding is automatic: Koin binds the class to all its supertypes. To bind only some of them, list them:

```kotlin
@Singleton(binds = [UserRepository::class])
class UserRepositoryImpl(private val database: Database) : UserRepository, Closeable
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val dataModule = module {
    singleOf(::UserRepositoryImpl) bind UserRepository::class

    // or declare the interface type directly
    single<UserRepository> { UserRepositoryImpl(get()) }
}
```

</TabItem>
</Tabs>

A consumer then asks for `UserRepository` and gets the `UserRepositoryImpl` instance. When two definitions bind the same type, you need a [qualifier](/docs/reference/koin-core/qualifiers) to tell them apart.

## Constructor parameters

Koin resolves each constructor parameter from the container. The parameter type decides how:

| Parameter | Resolved as | Detail |
|-----------|-------------|--------|
| `repository: UserRepository` | The `UserRepository` definition. Fails if it is missing | |
| `logger: Logger?` | The definition if it exists, `null` otherwise | |
| `heavy: Lazy<HeavyService>` | Created on first access of `heavy.value` | [Injection](/docs/reference/koin-core/injection) |
| `loggers: List<Logger>` | All definitions bound to `Logger` | [Advanced Patterns](/docs/reference/koin-core/advanced-patterns#collections-of-dependencies) |
| `@Named("local") db: Database` | The definition with that qualifier | [Qualifiers](/docs/reference/koin-core/qualifiers) |
| `@InjectedParam userId: String` | A value passed by the caller at resolution time | [Injected Parameters](/docs/reference/koin-core/injection-parameters) |
| `@Property("api_url") url: String` | A Koin property | [Starting Koin](/docs/reference/koin-core/starting-koin#properties) |

With the Compiler Plugin DSL and annotations, this works from the constructor signature alone:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class SyncService(
    private val repository: UserRepository,
    private val analytics: Analytics?,
    private val uploader: Lazy<Uploader>,
)

val appModule = module {
    single<SyncService>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class SyncService(
    private val repository: UserRepository,
    private val analytics: Analytics?,
    private val uploader: Lazy<Uploader>,
)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

`singleOf(::SyncService)` resolves every parameter with `get()`. For nullable, lazy or list parameters, write the call yourself with the matching function:

```kotlin
val appModule = module {
    single {
        SyncService(
            repository = get(),
            analytics = getOrNull(),
            uploader = inject(),
        )
    }
}
```

`getAll()` returns a `List`, `getProperty("key")` reads a property, and `params.get()` reads an injected parameter.

</TabItem>
</Tabs>

## Classes you don't own

Classes from libraries (Room, Retrofit, OkHttp) can't be annotated and often come from a builder. Declare them with a function whose parameters Koin resolves:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.plugin.module.dsl.create

fun database(context: Context): AppDatabase =
    Room.databaseBuilder(context, AppDatabase::class.java, "app-db").build()

fun topicDao(db: AppDatabase): TopicDao = db.topicDao()

val databaseModule = module {
    single { create(::database) }
    single { create(::topicDao) }
}
```

`create(::function)` resolves the function parameters like a constructor, with the same compile-time checks. Qualifier annotations on the function and its parameters work too.

</TabItem>
<TabItem value="annotations" label="Annotations">

Annotate a top-level function. `@ComponentScan` finds it like a class:

```kotlin
@Singleton
fun database(context: Context): AppDatabase =
    Room.databaseBuilder(context, AppDatabase::class.java, "app-db").build()

@Singleton
fun topicDao(db: AppDatabase): TopicDao = db.topicDao()
```

Or group provider functions in a `@Module` class:

```kotlin
@Module
class DatabaseModule {

    @Singleton
    fun database(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app-db").build()

    @Singleton
    fun topicDao(db: AppDatabase): TopicDao = db.topicDao()
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app-db").build()
    }
    single { get<AppDatabase>().topicDao() }
}
```

</TabItem>
</Tabs>

For complete Retrofit, Room and serialization setups on Android, see [Android Library Integration](/docs/reference/koin-android/binding-patterns).

## Eager creation

Koin creates a `single` the first time someone asks for it. To create it when Koin starts instead, mark it `createdAtStart`:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val appModule = module {
    single<ConfigManager>() withOptions {
        createdAtStart()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton(createdAtStart = true)
class ConfigManager
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val appModule = module {
    single(createdAtStart = true) { ConfigManager() }
}
```

</TabItem>
</Tabs>

Eager creation runs on the thread that starts Koin. On Android this is the main thread during `Application.onCreate()`, so every eager definition adds to app startup time. Keep it for objects that must exist before the first screen. To move work off startup, see [Lazy Modules](/docs/reference/koin-core/lazy-modules).

## Release callback

`onClose` runs when the instance is released: when Koin stops for a `single`, or when its scope closes for a `scoped`.

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val appModule = module {
    single<Database>() onClose { it?.close() }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

Annotations have no `onClose` parameter. Declare this definition in a DSL module.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val appModule = module {
    single { Database() } onClose { it?.close() }
}
```

</TabItem>
</Tabs>

`onClose` is never called for a `factory`: Koin does not keep factory instances.

## Overriding a definition

When two modules declare the same type, the module loaded last wins:

```kotlin
val prodModule = module {
    single<ApiService> { ProductionApi() }
}

val testModule = module {
    single<ApiService> { FakeApi() }
}

startKoin {
    modules(prodModule, testModule) // ApiService resolves to FakeApi
}
```

This is useful in tests, and a source of bugs when it happens by accident. To make overrides explicit, disable them globally and mark the ones you want:

```kotlin
val testModule = module {
    single<ApiService> { FakeApi() }.override()
}

startKoin {
    allowOverride(false)
    modules(prodModule, testModule)
}
```

With `allowOverride(false)`, an unmarked duplicate fails at start.

## Related

- [Modules](/docs/reference/koin-core/modules): group definitions and compose modules with `includes()`.
- [Qualifiers](/docs/reference/koin-core/qualifiers): several definitions of the same type.
- [Injection](/docs/reference/koin-core/injection): retrieve instances outside of constructors.
- [Scopes](/docs/reference/koin-core/scopes): instances bound to a lifecycle.
- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): what the Compiler Plugin checks in your definitions.
- [DSL Reference](/docs/reference/koin-core/dsl) and [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory): every keyword and annotation.
