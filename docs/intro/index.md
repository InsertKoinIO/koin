---
title: Introduction
---

# Welcome to Koin

**The Pragmatic Kotlin Dependency Injection Framework - Simple AND Powerful**

Koin is a lightweight dependency injection framework for Kotlin developers. Whether you're building Android apps, Kotlin Multiplatform projects, backend services with Ktor, or any Kotlin application, Koin makes dependency injection straightforward and intuitive.

## Why Koin?

Koin was designed with a clear philosophy: **you shouldn't have to choose between simplicity and capability**. With Koin, you get both.

### DSL & Annotations - Choose What You Want

Koin is powerful with both approaches. Prefer a clean Kotlin DSL? Use it. Like annotations? Use them. Both are first-class citizens, equally powerful.

| Value | What it means |
|-------|---------------|
| **Productive** | Easy to learn, easy to write. Get DI working in minutes, not hours |
| **Developer-Friendly** | DSL or Annotations - your choice. Clear errors, easy debugging, best DX |
| **Scalable** | Powers large enterprise applications with complex dependency graphs |
| **Safe** | Compile-time safety with Koin Compiler Plugin |
| **Dynamic** | Runtime flexibility: load modules dynamically, lazy loading, feature flags |

## Where to Start?

Choose your path based on your experience level:

### New to Dependency Injection?

Start with the fundamentals:
- **[What is Dependency Injection?](/docs/intro/what-is-dependency-injection)** - Understand the core concepts

### Know DI, New to Koin?

Jump right into Koin:
- **[What is Koin?](/docs/intro/what-is-koin)** - Discover Koin's approach to DI
- **[Koin Compiler Plugin](/docs/intro/koin-compiler-plugin)** - The recommended, safer way to use Koin

### Coming from Hilt/Dagger?

See how Koin compares:
- **[Koin vs Hilt/Dagger](/docs/intro/koin-vs-hilt)** - Understand the differences and migration path

### Ready to Code?

- **[Setup Guide](/docs/setup/gradle)** - Add Koin to your project
- **[Tutorials](/docs/quickstart/kotlin)** - Build your first Koin app
- **[Koin IDE Plugin](https://plugins.jetbrains.com/plugin/26131-koin-dependency-injection-official-)** - Install the official plugin for Android Studio & IntelliJ IDEA — code navigation, live safety checks, dependency graph visualization

## Koin's Approaches

Koin offers flexibility in how you define your dependencies:

| Approach | Status | Description |
|----------|--------|-------------|
| **Koin Compiler Plugin** (Kotlin 2.x) | Recommended | DSL: `single<MyService>()`, `factory<MyRepo>()`, `viewModel<MyVM>()`. |
| **Koin Compiler Plugin** (Kotlin 2.x) | Recommended | Annotations: `@Singleton`, `@Factory`, `@KoinViewModel`. Auto-detects dependencies, compile-time safety. |
| **Classic DSL** | Fully Supported | `singleOf(::MyService)`, `single { MyService(get()) }`. Works with any Kotlin version. Compiler Plugin adds safety on top when you're ready. |
| **KSP Processor** (`koin-ksp-compiler`) | Deprecated | Legacy processor for Koin Annotations. Migrate to Compiler Plugin — same annotations, native compiler integration. |

Learn more in [What is Koin?](/docs/intro/what-is-koin) and [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin).

## Platform Support

Koin works everywhere Kotlin runs:

| Platform | Package | Status |
|----------|---------|--------|
| **Kotlin/JVM** | `koin-core` | ✅ Full support |
| **Android** | `koin-android` | ✅ Full support |
| **Compose (Android & Multiplatform)** | `koin-compose` | ✅ Full support |
| **iOS** | `koin-core` | ✅ Full support |
| **Desktop** | `koin-core` | ✅ Full support |
| **Web (JS/Wasm)** | `koin-core` | ✅ Full support |
| **Ktor** | `koin-ktor` | ✅ Full support |

## Quick Example

Here's a taste of what Koin looks like:

```kotlin
// Define your classes
class UserRepository(private val api: ApiService)
class UserViewModel(private val repository: UserRepository) : ViewModel()

// Define your module with Compiler Plugin DSL
val appModule = module {
    single<ApiService>()
    single<UserRepository>()
    viewModel<UserViewModel>()
}

// Start Koin
startKoin {
    modules(appModule)
}

// Inject in your Activity
class MainActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModel()
}
```

Or with annotations:

```kotlin
@Singleton
class UserRepository(private val api: ApiService)

@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()

@Module
@ComponentScan("com.myapp")
class AppModule
```

Ready to get started? Head to the [Setup Guide](/docs/setup/gradle).
