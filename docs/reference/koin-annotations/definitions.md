---
title: Definitions with Annotations
---


Koin Annotations allow declaring the same kind of definitions as the regular Koin DSL, but with annotations. Just tag your class with the needed annotation, and it will generate everything for you!

For example, the equivalent of `single { MyComponent(get()) }` DSL declaration is just done by tagging with `@Single` like this:

```kotlin
@Single
class MyComponent(val myDependency : MyDependency)
```

Koin Annotations keep the same semantics as the Koin DSL. You can declare your components with the following definitions:

- `@Single` - singleton instance (declared with `single { }` in DSL)
- `@Factory` - factory instance. For instance recreated each time you need an instance. (declared with `factory { }` in DSL)
- `@KoinViewModel` - Android ViewModel instance (declared with `viewModel { }` in DSL)
- `@KoinWorker` - Android Worker Workmanager instance (declared with `worker { }` in DSL)

For Scopes, check the [Declaring Scopes](/docs/reference/koin-core/scopes) section.

### ViewModel for Kotlin Multiplatform

The `@KoinViewModel` annotation generates ViewModels using the unified `koin-core-viewmodel` API, providing Kotlin Multiplatform compatibility.

```kotlin
@KoinViewModel
class UserViewModel(val repository: UserRepository) : ViewModel()
```

This generates `viewModel` definitions compatible with both Android and Compose Multiplatform.

## Automatic or Specific Binding

When declaring a component, all detected "bindings" (associated supertypes) will already be prepared for you. For example, the following definition:

```kotlin
@Single
class MyComponent(val myDependency : MyDependency) : MyInterface
```

Koin will declare that your `MyComponent` component is also tied to `MyInterface`. The DSL equivalent is `single { MyComponent(get()) } bind MyInterface::class`.


Instead of letting Koin detect things for you, you can also specify what type you really want to bind with the `binds` annotation parameter:

 ```kotlin
@Single(binds = [MyBoundType::class])
```

## Nullable Dependencies

If your component is using nullable dependency, don't worry it will be handled automatically for you. Keep using your definition annotation, and Koin will guess what to do:

```kotlin
@Single
class MyComponent(val myDependency : MyDependency?)
```

The generated DSL equivalent will be `single { MyComponent(getOrNull()) }`


> Note that this also works for injected Parameters and properties

## Qualifier with @Named

You can add a "name" to a definition (also called qualifier), to make a distinction between several definitions for the same type, with the `@Named` annotation:

```kotlin
@Single
@Named("InMemoryLogger")
class LoggerInMemoryDataSource : LoggerDataSource

@Single
@Named("DatabaseLogger")
class LoggerLocalDataSource(private val logDao: LogDao) : LoggerDataSource
```

When resolving a dependency, just use the qualifier with `named` function:

```kotlin
val logger: LoggerDataSource by inject(named("InMemoryLogger"))
```

It is also possible to create custom qualifier annotations. Using the previous example:

```kotlin
@Named
annotation class InMemoryLogger

@Named
annotation class DatabaseLogger

@Single
@InMemoryLogger
class LoggerInMemoryDataSource : LoggerDataSource

@Single
@DatabaseLogger
class LoggerLocalDataSource(private val logDao: LogDao) : LoggerDataSource
```

```kotlin
val logger: LoggerDataSource by inject(named<InMemoryLogger>())
```

## Injected Parameters with @InjectedParam

You can tag a constructor member as "injected parameter", which means that the dependency will be passed in the graph when calling for resolution.

For example:

```kotlin
@Single
class MyComponent(@InjectedParam val myDependency : MyDependency)
```

Then you can call your `MyComponent` and pass an instance of `MyDependency`:

```kotlin
val m = MyDependency()
// Resolve MyComponent while passing MyDependency
koin.get<MyComponent> { parametersOf(m) }
```

The generated DSL equivalent will be `single { params -> MyComponent(params.get()) }`


## Injecting a lazy dependency - `Lazy<T>`

Koin can automatically detect and resolve a lazy dependency. Here, for example, we want to resolve lazily the `LoggerDataSource` definition. You just need to use the `Lazy` Kotlin type as follows:

```kotlin
@Single
class LoggerInMemoryDataSource : LoggerDataSource

@Single
class LoggerAggregator(val lazyLogger : Lazy<LoggerDataSource>)
```

Behind it will generate the DSL like with `inject()` instead of `get()`:

```kotlin
single { LoggerAggregator(inject()) }
```

## Injecting a list of dependencies - `List<T>`

Koin can automatically detect and resolve a list of dependencies. Here, for example, we want to resolve all `LoggerDataSource` definitions. You just need to use the `List` Kotlin type as follows:

```kotlin
@Single
@Named("InMemoryLogger")
class LoggerInMemoryDataSource : LoggerDataSource

@Single
@Named("DatabaseLogger")
class LoggerLocalDataSource(private val logDao: LogDao) : LoggerDataSource

@Single
class LoggerAggregator(val datasource : List<LoggerDataSource>)
```

Behind it will generate the DSL, like with `getAll()` function:

```kotlin
single { LoggerAggregator(getAll()) }
```

## Properties with @Property

To resolve a Koin property in your definition, just tag a constructor member with `@Property`. This will resolve the Koin property thanks to the value passed to the annotation:

```kotlin
@Factory
public class ComponentWithProps(
    @Property("id") public val id : String
)
```

The generated DSL equivalent will be `factory { ComponentWithProps(getProperty("id")) }`

### @PropertyValue - Property with default value (since 1.4)

Koin Annotations offers you the possibility to define a default value for a property, directly from your code with `@PropertyValue` annotation.
Let's follow our sample:

```kotlin
@Factory
public class ComponentWithProps(
    @Property("id") public val id : String
){
    public companion object {
        @PropertyValue("id")
        public const val DEFAULT_ID : String = "_empty_id"
    }
}
```

The generated DSL equivalent will be `factory { ComponentWithProps(getProperty("id", ComponentWithProps.DEFAULT_ID)) }`

## JSR-330 Compatibility Annotations

Koin Annotations provides JSR-330 (Jakarta Inject) compatible annotations through the `koin-jsr330` module. These annotations are particularly useful for developers migrating from other JSR-330 compatible frameworks like Hilt, Dagger, or Guice.

### Setup

Add the `koin-jsr330` dependency to your project:

```kotlin
dependencies {
    implementation "io.insert-koin:koin-jsr330:$koin_version"
}
```

### Available JSR-330 Annotations

#### @Singleton (jakarta.inject.Singleton)

JSR-330 standard singleton annotation, equivalent to Koin's `@Single`:

```kotlin
import jakarta.inject.Singleton

@Singleton
class DatabaseService
```

This generates the same result as `@Single` - a singleton instance in Koin.

#### @Named (jakarta.inject.Named)

JSR-330 standard qualifier annotation for string-based qualifiers:

```kotlin
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
@Named("inMemory")
class InMemoryCache : Cache

@Singleton  
@Named("redis")
class RedisCache : Cache
```

#### @Inject (jakarta.inject.Inject)

JSR-330 standard injection annotation. While Koin Annotations doesn't require explicit constructor marking, `@Inject` can be used for JSR-330 compatibility:

```kotlin
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class UserService @Inject constructor(
    private val repository: UserRepository
)
```

#### @Qualifier (jakarta.inject.Qualifier)

Meta-annotation for creating custom qualifier annotations:

```kotlin
import jakarta.inject.Qualifier

@Qualifier
annotation class Database

@Qualifier  
annotation class Cache

@Singleton
@Database
class DatabaseConfig

@Singleton
@Cache  
class CacheConfig
```

#### @Scope (jakarta.inject.Scope)

Meta-annotation for creating custom scope annotations:

```kotlin
import jakarta.inject.Scope

@Scope
annotation class RequestScoped

// Use with Koin's scope system
@Scope(name = "request") 
@RequestScoped
class RequestProcessor
```

### Mixed Usage

You can freely mix JSR-330 annotations with Koin annotations in the same project:

```kotlin
// JSR-330 style
@Singleton
@Named("primary")
class PrimaryDatabase : Database

// Koin style  
@Single
@Named("secondary")
class SecondaryDatabase : Database

// Mixed in same class
@Factory
class DatabaseManager @Inject constructor(
    @Named("primary") private val primary: Database,
    @Named("secondary") private val secondary: Database  
)
```

### Framework Migration Benefits

Using JSR-330 annotations provides several advantages for framework migration:

- **Familiar API**: Developers coming from Hilt, Dagger, or Guice can use known annotations
- **Gradual Migration**: Existing JSR-330 annotated code works with minimal changes
- **Standard Compliance**: Following JSR-330 ensures compatibility with dependency injection standards
- **Team Onboarding**: Easier for teams familiar with other DI frameworks

:::info
JSR-330 annotations in Koin generate the same underlying DSL as their Koin equivalents. The choice between JSR-330 and Koin annotations is purely stylistic and based on team preferences or migration requirements.
:::
