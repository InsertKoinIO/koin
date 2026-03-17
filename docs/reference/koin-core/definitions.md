---
title: Definitions
---

# Definitions

Definitions declare how Koin creates and manages your dependencies. This guide covers all definition types using both DSL and Annotations.

## Definition Types

| Type | DSL | Annotation | Lifecycle | Use Case |
|------|-----|------------|-----------|----------|
| Singleton | `single()` | `@Singleton` | One instance for app lifetime | Services, repositories, databases |
| Factory | `factory()` | `@Factory` | New instance each time | Presenters, use cases, stateful objects |
| Scoped | `scoped()` | `@Scoped` | One instance per scope | Activity-bound, session-bound objects |
| ViewModel | `viewModel()` | `@KoinViewModel` | Android ViewModel lifecycle | ViewModels |

## Declaring Definitions

### Compiler Plugin DSL (Recommended)

```kotlin
import org.koin.plugin.module.dsl.*

val appModule = module {
    // Singleton
    single<Database>()
    single<UserRepository>()

    // Factory - new instance each time
    factory<UserPresenter>()

    // ViewModel
    viewModel<UserViewModel>()
}
```

### Annotations

```kotlin
@Singleton  // or @Single
class Database

@Singleton
class UserRepository(private val database: Database)

@Factory
class UserPresenter(private val repository: UserRepository)

@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

### Classic DSL

```kotlin
val appModule = module {
    // With constructor reference (autowiring)
    singleOf(::Database)
    singleOf(::UserRepository)
    factoryOf(::UserPresenter)
    viewModelOf(::UserViewModel)

    // With lambda (manual wiring)
    single { Database() }
    single { UserRepository(get()) }
    factory { UserPresenter(get()) }
    viewModel { UserViewModel(get()) }
}
```

## Definition Comparison

| Concept | Compiler Plugin DSL | Classic DSL | Annotation |
|---------|---------------------|-------------|------------|
| Singleton | `single<MyClass>()` | `singleOf(::MyClass)` | `@Singleton` / `@Single` |
| Factory | `factory<MyClass>()` | `factoryOf(::MyClass)` | `@Factory` |
| Scoped | `scoped<MyClass>()` | `scopedOf(::MyClass)` | `@Scoped` |
| ViewModel | `viewModel<MyVM>()` | `viewModelOf(::MyVM)` | `@KoinViewModel` |
| Worker | `worker<MyWorker>()` | `workerOf(::MyWorker)` | `@KoinWorker` |

:::info
The compiler plugin is analysing your class and function parameters, to generate the right call to Koin with `get()` function you don't have to write anymore.
:::

## Single (Singleton)

Creates one instance that's reused throughout the app:

```kotlin
// DSL
single<DatabaseHelper>()

// Annotation
@Singleton
class DatabaseHelper
```

Both create the same result - a single instance shared across all consumers.

## Factory

Creates a new instance each time:

```kotlin
// DSL
factory<UserPresenter>()

// Annotation
@Factory
class UserPresenter(private val repository: UserRepository)
```

## Scoped

Creates one instance per scope:

```kotlin
// DSL
scope<MyActivity> {
    scoped<ActivityPresenter>()
}

// Annotation
@Scoped(MyActivityScope::class)
class ActivityPresenter
```

## ViewModel

Android ViewModel with proper lifecycle:

```kotlin
// DSL
viewModel<UserViewModel>()

// Annotation
@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

## Interface Binding

### Compiler Plugin DSL

```kotlin
single<UserRepositoryImpl>() bind UserRepository::class

// Multiple bindings
single<MyServiceImpl>() binds arrayOf(ServiceA::class, ServiceB::class)
```

### Classic DSL

```kotlin
singleOf(::UserRepositoryImpl) bind UserRepository::class

// Or with lambda
single<UserRepository> { UserRepositoryImpl(get()) }
```

### Annotations

**Interface binding is automatic** when your class implements an interface:

```kotlin
@Singleton
class UserRepositoryImpl(
    private val database: Database
) : UserRepository  // Automatically binds to UserRepository
```

For explicit binding:

```kotlin
@Singleton
@Binds(UserRepository::class)
class UserRepositoryImpl : UserRepository
```

## Qualifiers (Named Definitions)

When you have multiple definitions of the same type. See also [Injection with Qualifiers](/docs/reference/koin-core/injection#injection-with-qualifiers) for retrieval.

### Compiler Plugin DSL

With Compiler Plugin DSL, you need to annotate with `@Named` to use string qualifier (like you were previously using `named()`)

```kotlin
@Named("local")
class LocalDatabase : Database

@Named("remote")
class RemoteDatabase : Database

class UserRepository(
    @Named("local") private val localDb: Database,
    @Named("remote") private val remoteDb: Database
)

single<LocalDatabase>()
single<RemoteDatabase>()
single<UserRepository>()

// Usage
val localDb: Database = get(named("local"))
```

### Classic DSL

```kotlin
single<Database>(named("local")) { LocalDatabase() }
single<Database>(named("remote")) { RemoteDatabase() }

// Usage
val localDb: Database = get(named("local"))
```

### Annotations

```kotlin
@Singleton
@Named("local")
class LocalDatabase : Database

@Singleton
@Named("remote")
class RemoteDatabase : Database

// In a consumer
@Singleton
class UserRepository(
    @Named("local") private val localDb: Database,
    @Named("remote") private val remoteDb: Database
)
```

## Injected Parameters

Pass parameters at injection time:

### Compiler Plugin DSL

Use the `@InjectedParam` to indicate that a parameter will be served by injected parameters.

```kotlin
class UserPresenter(
    @InjectedParam userId : String,
    repository : UserRepository
)

factory<UserPresenter>()
```

### Classic DSL

```kotlin
class UserPresenter(
    userId : String,
    repository : UserRepository
)

factory { params ->
    UserPresenter(
        userId = params.get(),
        repository = get()
    )
}
```

### Annotations

```kotlin
@Factory
class UserPresenter(
    @InjectedParam val userId: String,
    val repository: UserRepository  // Auto-injected
)

// Usage
val presenter: UserPresenter = get { parametersOf("user123") }
```

## Optional Dependencies

### Compiler Plugin DSL

```kotlin
class MyService(
    val required: RequiredDep,
    val optional: OptionalDep?  // Resolved with getOrNull()
)

single<MyService>()
```

### Classic DSL

```kotlin
single {
    MyService(
        required = get(),
        optional = getOrNull()
    )
}
```

### Annotations

Nullable parameters are handled automatically:

```kotlin
@Singleton
class MyService(
    val required: RequiredDep,
    val optional: OptionalDep?  // Resolved with getOrNull()
)
```

## Lazy Injection

Defer instance creation:

### Compiler Plugin DSL

```kotlin
class MyService(
    val lazyDep: Lazy<HeavyDependency>  // Deferred creation
)

single<MyService>()
```

### Classic DSL

```kotlin
single {
    MyService(
        lazyDep = inject()  // Lazy<Dependency>
    )
}
```

### Annotations

```kotlin
@Singleton
class MyService(
    val lazyDep: Lazy<HeavyDependency>  // Deferred creation
)
```

## Properties

Inject configuration values:

### Compiler Plugin DSL

```kotlin
class ApiClient(
    @Property("api_url") val url: String,
    @Property("api_key") val key: String
)

single<ApiClient>()
```

### Classic DSL

```kotlin
single {
    ApiClient(
        url = getProperty("api_url"),
        key = getProperty("api_key", "default")
    )
}
```

### Annotations

```kotlin
@Singleton
class ApiClient(
    @Property("api_url") val url: String,
    @Property("api_key") val key: String
)
```

## Callbacks

### onClose Callback

Execute code when instance is released:

```kotlin
single {
    Database()
} onClose {
    it?.close()  // Called when Koin stops or scope closes
}
```

### createdAtStart

Create instance eagerly at startup:

```kotlin
// Compiler Plugin DSL
single<ConfigManager>() withOptions {
    createdAtStart()
}

// Classic DSL
single(createdAtStart = true) {
    ConfigManager()
}
```

## Definition Override

### Default: Last Wins

```kotlin
val prodModule = module {
    single<ApiService> { ProductionApi() }
}

val testModule = module {
    single<ApiService> { MockApi() }  // Overrides production
}

startKoin {
    modules(prodModule, testModule)
}
```

### Explicit Override

In strict mode, mark overrides explicitly:

```kotlin
val testModule = module {
    single<ApiService> { MockApi() }.override()
}

startKoin {
    allowOverride(false)
    modules(prodModule, testModule)
}
```

## Best Practices

1. **Prefer Constructor Injection** - Makes code testable without Koin
2. **Use `single` for stateless services** - Repositories, clients, helpers
3. **Use `factory` for stateful objects** - Presenters, use cases with state
4. **Use `scoped` for lifecycle-bound objects** - Activity, Fragment, Session
5. **Minimize qualifiers** - Use different interfaces instead when possible
6. **Bind to interfaces** - Depend on abstractions, not implementations
7. **Use `create(::builder)` for external libraries** - Safer dependency resolution

## Next Steps

- **[Injection](/docs/reference/koin-core/injection)** - Retrieve dependencies
- **[Qualifiers](/docs/reference/koin-core/qualifiers)** - Named and typed qualifiers
- **[Advanced Patterns](/docs/reference/koin-core/advanced-patterns)** - Collections, decorators, external libraries
- **[Scopes](/docs/reference/koin-core/scopes)** - Manage lifecycle
