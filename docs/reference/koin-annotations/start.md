---
title: Starting with Koin Annotations
---

Koin Annotations let you declare definitions using annotations on your classes. The Koin Compiler Plugin processes these annotations and generates all underlying Koin DSL for you at compile time.

## Getting Started

Not familiar with Koin? First, take a look at [Koin Getting Started](https://insert-koin.io/docs/quickstart/kotlin/)

### Setup

Add the Koin Compiler Plugin to your project. See [Compiler Plugin Setup](/docs/setup/compiler-plugin) for complete instructions.

```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
}
```

### Annotating Components

Tag your components with definition annotations:

```kotlin
@Singleton
class MyRepository

@Singleton
class MyService(val repository: MyRepository)

@Factory
class MyUseCase(val service: MyService)
```

### Declaring Modules

Create a module to organize your definitions:

```kotlin
@Module
@ComponentScan("com.myapp")
class AppModule
```

### Starting Koin

Use `@KoinApplication` with the typed startup API:

```kotlin
@KoinApplication(modules = [AppModule::class])
class MyApp

fun main() {
    startKoin<MyApp> {
        printLogger()
    }

    // Use your Koin API as usual
    KoinPlatform.getKoin().get<MyService>()
}
```

## Configuration Labels

Use `@Configuration` to create modules that load based on labels:

```kotlin
@Module
@Configuration  // Default configuration
class CoreModule

@Module
@Configuration("prod")
class ProdModule

@Module
@Configuration("test")
class TestModule
```

Load specific configurations:

```kotlin
@KoinApplication(
    modules = [CoreModule::class],
    configurations = ["prod"]  // Only loads @Configuration("prod") modules
)
class ProdApp

fun main() {
    startKoin<ProdApp>()
}
```

## Typed Startup APIs

The Compiler Plugin provides typed APIs for starting Koin:

| API | Description |
|-----|-------------|
| `startKoin<T>()` | Start Koin globally |
| `startKoin<T> { }` | Start with configuration block |
| `koinApplication<T>()` | Create isolated KoinApplication |
| `koinConfiguration<T>()` | Create configuration (for Compose, Ktor) |

Where `T` is a class annotated with `@KoinApplication`.

## Compile-Time Safety

The Compiler Plugin verifies your Koin configuration at compile time, checking that all dependencies are declared and accessible.

### Bypass with @Provided

Use `@Provided` to indicate a dependency is provided externally:

```kotlin
class ExternalComponent  // Declared elsewhere

@Factory
class MyPresenter(@Provided val external: ExternalComponent)
```

## Compiler Plugin Options

See **[Compiler Plugin Options](/docs/reference/koin-annotations/options)** for all configuration options.

## ProGuard Rules

For SDK development with ProGuard/R8:

```
# Keep annotation definitions
-keep class org.koin.core.annotation.** { *; }

# Keep classes annotated with Koin annotations
-keep @org.koin.core.annotation.* class * { *; }
```

## See Also

- **[Compiler Plugin Setup](/docs/setup/compiler-plugin)** - Complete setup guide
- **[Definitions](/docs/reference/koin-annotations/definitions)** - All definition annotations
- **[Modules](/docs/reference/koin-annotations/modules)** - Module organization
- **[KMP Support](/docs/reference/koin-annotations/kmp)** - Kotlin Multiplatform
