---
title: What is Koin?
---

# What is Koin?

### The Pragmatic Kotlin Dependency Injection Framework - Simple AND Powerful

Koin is a lightweight dependency injection framework designed specifically for Kotlin. Unlike traditional DI frameworks that rely on code generation or reflection, Koin offers two equally powerful approaches: a clean **Kotlin DSL** and intuitive **Annotations**. Choose what fits your team - both are first-class citizens.

## Koin's Core Values

| Value | What it means |
|-------|---------------|
| **Productive** | Easy to learn, easy to write. Get DI working in minutes, not hours |
| **Developer-Friendly** | DSL or Annotations - your choice. Clear errors, easy debugging, best DX |
| **Scalable** | Powers large enterprise applications with complex dependency graphs |
| **Safe** | Compile-time safety with Koin Compiler Plugin |
| **Dynamic** | Runtime flexibility: load modules dynamically, lazy loading, feature flags |

## Why Developers Love Koin

- **Learn in minutes** - No complex concepts, intuitive DSL and simple annotations
- **Write less code** - DSL or Annotations, Compiler Plugin auto-wires dependencies
- **Choose your style** - DSL for Kotlin purists, Annotations for familiar patterns - both equally powerful
- **Debug easily** - Clear error messages, no generated code to trace through
- **Scale confidently** - Used in production by enterprises worldwide
- **Stay safe** - Compile-time verification catches errors before runtime
- **Stay flexible** - Runtime-based but performant. Dynamic modules, lazy loading, feature flags
- **IDE support** - Official plugin for Android Studio & IntelliJ IDEA — navigate definitions, live safety checks, graph visualization

## Two Styles, One Framework - Both Equally Powerful

Koin supports two styles of defining dependencies. Both are first-class citizens with full feature parity. Choose what fits your team:

### DSL Style

Define dependencies using Kotlin DSL syntax:

```kotlin
val appModule = module {
    single<Database>()
    single<ApiClient>()
    single<UserRepository>()
    viewModel<UserViewModel>()
}
```

### Annotation Style

Define dependencies using annotations:

```kotlin
@Singleton
class Database

@Singleton
class ApiClient

@Singleton
class UserRepository(
    private val database: Database,
    private val apiClient: ApiClient
)

@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

Both styles are processed by the **Koin Compiler Plugin** for compile-time safety.

## Koin's Annotations Are Simpler

If you've used Hilt or Dagger, you'll notice Koin annotations require less ceremony:

| Task | Koin | Hilt |
|------|------|------|
| **Singleton** | `@Singleton class MyService` | `@Singleton class MyService @Inject constructor(...)` |
| **Interface binding** | Automatic (just implement the interface) | Requires `@Binds` in abstract module |
| **Component scanning** | `@ComponentScan("package")` | Not available |
| **Module discovery** | `@Configuration` - auto-discovered | Manual `@InstallIn` per module |

**Example comparison:**

```kotlin
// KOIN - That's it!
@Singleton
class MyRepository(val api: ApiService)

@Module
@ComponentScan("com.app")
class AppModule
```

```kotlin
// HILT - More ceremony
@Singleton
class MyRepository @Inject constructor(val api: ApiService)

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindRepository(impl: MyRepository): Repository
}
```

## Powered by Koin Compiler Plugin

The **Koin Compiler Plugin** is the recommended way to use Koin for all new projects:

- **Native Kotlin Compiler Plugin (K2)** - Not KSP, direct compiler integration
- **Auto-detects constructor parameters** - Less manual wiring
- **Compile-time safety** - Errors caught during build
- **Works with both DSL and Annotations** - Your choice
- **Simple setup** - One Gradle plugin

### Cleaner Syntax with Compiler Plugin

| Classic DSL | Compiler Plugin DSL |
|-------------|---------------------|
| `singleOf(::MyService)` | `single<MyService>()` |
| `single { MyService(get(), get()) }` | `single<MyService>()` |
| `factoryOf(::MyRepo)` | `factory<MyRepo>()` |
| `viewModelOf(::MyVM)` | `viewModel<MyVM>()` |

Learn more in [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin).

## Classic DSL (Fully Supported)

The classic DSL remains fully supported for all Kotlin versions:

```kotlin
val appModule = module {
    singleOf(::Database)
    singleOf(::ApiClient)
    singleOf(::UserRepository)
    viewModelOf(::UserViewModel)
}
```

Or with explicit wiring:

```kotlin
val appModule = module {
    single { Database() }
    single { ApiClient() }
    single { UserRepository(get(), get()) }
    viewModel { UserViewModel(get()) }
}
```

:::info
Classic DSL is not deprecated. Koin works perfectly with it. The Compiler Plugin adds compile-time analysis on top when you're ready to migrate.
:::

## Koin Annotations is now part of the Koin project

The `koin-annotations` library — `@Singleton`, `@Factory`, `@KoinViewModel`, `@Module`, `@ComponentScan`, and the rest — ships under the main Koin version and is fully supported. It is **not** deprecated.

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-annotations") // same Koin version
}
```

Your annotations are processed by the **Koin Compiler Plugin** — see [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin) and the [Annotations Reference](/docs/reference/koin-annotations/start).

## Koin KSP Compiler is deprecated in favor of Koin Compiler Plugin

:::info
The legacy KSP processor `koin-ksp-compiler` is **deprecated** and will be removed in a future Koin version. The replacement is the **Koin Compiler Plugin** — native K2 compiler integration, no generated files, simpler KMP setup.
:::

If you're using Koin Annotations with `koin-ksp-compiler`, migrate to the Compiler Plugin:

- **Same annotations** — No code changes needed
- **Better processing** — Native compiler integration, no generated files
- **Simpler setup** — No KSP configuration

See [Migrating from KSP to Compiler Plugin](/docs/migration/from-ksp-to-compiler-plugin).

## Runtime + Compile Safe = Best of Both Worlds

Koin is **runtime-based but performant and compile-safe**. This unique combination enables:

**Compile-time safety** (with Compiler Plugin):
- Validates your dependency graph during build
- Auto-detects constructor parameters
- Catches missing dependencies before runtime

**Runtime flexibility** (that compile-time-only frameworks can't offer):
- Dynamic module loading/unloading
- Lazy module loading (background)
- Feature flag driven injection
- Plugin architectures
- A/B testing with different implementations

```kotlin
// Dynamic module loading - impossible with Hilt
if (featureEnabled) {
    loadKoinModules(premiumFeatureModule)
}

// Later, if feature disabled
unloadKoinModules(premiumFeatureModule)
```

## Who is Koin For?

Koin is ideal for:

- **Teams who value productivity** - Less boilerplate, faster development
- **Android developers** wanting cleaner DI than Hilt/Dagger
- **Kotlin Multiplatform projects** - Android, iOS, Desktop, Web, Backend
- **Enterprise projects** that need to scale
- **Anyone who believes DI shouldn't be complicated**

## Next Steps

- **[What is Dependency Injection?](/docs/intro/what-is-dependency-injection)** - Learn DI fundamentals
- **[Koin Compiler Plugin](/docs/intro/koin-compiler-plugin)** - The recommended approach
- **[Setup Guide](/docs/setup/gradle)** - Add Koin to your project
- **[Tutorials](/docs/tutorials/your-first-app)** - Build your first app with Koin
