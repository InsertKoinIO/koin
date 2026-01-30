---
title: Application, Configuration and Modules 
---

## Application Bootstrap with @KoinApplication

Use `@KoinApplication` to define your application entry point:

```kotlin
@KoinApplication(modules = [MyModule::class])
class MyApp
```

Start Koin using the typed API:

```kotlin
fun main() {
    startKoin<MyApp>()

    // Or with configuration
    startKoin<MyApp> {
        printLogger()
    }
}
```

### Available Typed APIs

| API | Description |
|-----|-------------|
| `startKoin<T>()` | Start Koin globally |
| `startKoin<T> { }` | Start with configuration block |
| `koinApplication<T>()` | Create isolated KoinApplication |
| `koinConfiguration<T>()` | Create configuration (for Compose, Ktor) |

### @KoinApplication Parameters

- `modules`: Array of module classes to include
- `configurations`: Array of configuration labels to load

```kotlin
@KoinApplication(
    modules = [CoreModule::class],
    configurations = ["production"]
)
class ProdApp
```

:::info
When no configurations are specified, modules marked with `@Configuration` (default label) are loaded automatically.
:::

## Configuration Management with @Configuration

The `@Configuration` annotation allows you to organize modules into different configurations (environments, flavors, etc.). This is useful for organizing modules by deployment environment or feature sets.

### Basic Configuration Usage

```kotlin
// Put module in default Configuration
@Module
@Configuration
class CoreModule
```

:::info
The default configuration is named "default", can be used with `@Configuration` or `@Configuration("default")`
:::

You need to use the `@KoinApplication` to be able to scan modules from configuration:

```kotlin
// module A
@Module
@Configuration
class ModuleA

// module B
@Module
@Configuration
class ModuleB

// module App, scan all @Configuration modules
@KoinApplication
object MyApp
```


### Multiple Configuration Support

A module can be associated with multiple configurations:

```kotlin
// This module is available in both "prod" and "test" configurations
@Module
@Configuration("prod", "test")
class DatabaseModule {
    @Single
    fun database() = PostgreSQLDatabase()
}

// This module is available in default, test, and development
@Module
@Configuration("default", "test", "development") 
class LoggingModule {
    @Single
    fun logger() = Logger()
}
```

### Environment-Specific Configurations

```kotlin
// Development-only configuration
@Module
@Configuration("development")
class DevDatabaseModule {
    @Single
    fun database() = InMemoryDatabase()
}

// Production-only configuration  
@Module
@Configuration("production")
class ProdDatabaseModule {
    @Single
    fun database() = PostgreSQLDatabase()
}

// Available in multiple environments
@Module
@Configuration("default", "production", "development")
class CoreModule {
    @Single
    fun logger() = Logger()
}
```

### Using Configurations with @KoinApplication

By default, the `@KoinApplication` is loading all default configurations (modules tagged with `@Configuration`)

You can also reference these configurations in your application bootstrap:

```kotlin
@KoinApplication(configurations = ["default", "production"])
class ProductionApp

@KoinApplication(configurations = ["default", "development"])  
class DevelopmentApp

// Load only default configuration (same as @KoinApplication with no parameters)
@KoinApplication
class SimpleApp
```

:::info
- Empty `@Configuration` is equivalent to `@Configuration("default")`
- The "default" configuration is loaded automatically when no specific configurations are specified
- Modules can belong to multiple configurations by listing them in the annotation
:::


## Organizing with Modules

Always organize your definitions in explicit modules using `@Module`:

## Class Module with @Module

To declare a module, tag a class with `@Module` annotation:

```kotlin
@Module
class MyModule
```

Reference modules in your `@KoinApplication`:

```kotlin
@KoinApplication(modules = [MyModule::class])
class MyApp

fun main() {
    startKoin<MyApp>()
}
```

## Components Scan with @ComponentScan

Use `@ComponentScan` to automatically discover annotated components:

```kotlin
@Module
@ComponentScan
class MyModule
```

This scans the current package and subpackages for annotated components. Specify a package explicitly:

```kotlin
@Module
@ComponentScan("com.myapp.features")
class FeatureModule
```

:::info
`@ComponentScan` traverses across all Gradle modules for the same package.
:::

## Definitions in Class Modules

To define a definition directly in your code, you can annotate a function with definition annotations:

```kotlin
// given 
// class MyComponent(val myDependency : MyDependency)

@Module
class MyModule {

  @Single
  fun myComponent(myDependency : MyDependency) = MyComponent(myDependency)
}
```

> **Note**: `@InjectedParam` (for injected parameters from startKoin) and `@Property` (for property injection) are also usable on function members. See the definitions documentation for more details on these annotations.


## Including Modules

Use the `includes` attribute to compose modules:

```kotlin
@Module
class ModuleA

@Module(includes = [ModuleA::class])
class ModuleB
```

Reference the root module in your application:

```kotlin
@KoinApplication(modules = [ModuleB::class])  // Includes ModuleA automatically
class MyApp

fun main() {
    startKoin<MyApp>()
}
```
