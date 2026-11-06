---
title: Multi-Module Apps
description: Organize Koin modules in an Android app split into app, feature and core Gradle modules, including dynamic feature modules.
---

This page shows how to organize Koin in an Android app split into Gradle modules (`:app`, `:feature:*`, `:core:*`). It covers where each Koin module lives, how the app loads them, how features share types, and dynamic feature modules. Module composition (`includes()`, public and private modules, `@Configuration`) is explained in [Modules](/docs/reference/koin-core/modules).

## Project layout

```
:app                  Application class, startKoin, appModule
:feature:login        LoginViewModel, LoginUseCase, loginModule
:feature:profile      ProfileViewModel, profileModule
:core:data            repositories, Retrofit, Room, dataModule
:core:domain          interfaces shared by features (UserRepository)
```

Each Gradle module declares the Koin module for its own classes. Classes can then stay `internal` to their Gradle module: only the Koin module is public. The `:app` module is the only one that knows every feature, so it is where Koin starts.

## Declaring and loading feature modules

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
// Gradle module ":feature:login"
internal class LoginUseCase(private val auth: AuthService)
internal class LoginViewModel(private val login: LoginUseCase) : ViewModel()

val loginModule = module {
    factory<LoginUseCase>()
    viewModel<LoginViewModel>()
}
```

```kotlin
// Gradle module ":app"
val appModule = module {
    includes(dataModule, loginModule, profileModule)
}

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
// Gradle module ":feature:login"
@Module
@ComponentScan("com.app.feature.login")
@Configuration
class LoginModule

@Factory
internal class LoginUseCase(private val auth: AuthService)

@KoinViewModel
internal class LoginViewModel(private val login: LoginUseCase) : ViewModel()
```

```kotlin
// Gradle module ":app"
@KoinApplication
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<MainApplication> {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}
```

`startKoin<MainApplication>()` finds every `@Configuration` module on the app classpath, including those from feature Gradle modules. Adding a feature means adding the Gradle dependency, nothing else. To list modules explicitly instead, use `@KoinApplication(modules = [LoginModule::class, ProfileModule::class])`.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
// Gradle module ":feature:login"
internal class LoginUseCase(private val auth: AuthService)
internal class LoginViewModel(private val login: LoginUseCase) : ViewModel()

val loginModule = module {
    factoryOf(::LoginUseCase)
    viewModelOf(::LoginViewModel)
}
```

```kotlin
// Gradle module ":app"
val appModule = module {
    includes(dataModule, loginModule, profileModule)
}

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
```

</TabItem>
</Tabs>

## Sharing types between features

A feature should not depend on another feature. When `:feature:profile` needs the current user, it depends on an interface in a core module, and the implementation is bound where it lives:

```kotlin
// Gradle module ":core:domain"
interface UserRepository {
    suspend fun currentUser(): User
}
```

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
// Gradle module ":core:data"
internal class UserRepositoryImpl(private val api: ApiService) : UserRepository

val dataModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
// Gradle module ":core:data"
@Singleton
internal class UserRepositoryImpl(private val api: ApiService) : UserRepository
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
// Gradle module ":core:data"
internal class UserRepositoryImpl(private val api: ApiService) : UserRepository

val dataModule = module {
    singleOf(::UserRepositoryImpl) bind UserRepository::class
}
```

</TabItem>
</Tabs>

`ProfileViewModel(private val users: UserRepository)` in `:feature:profile` then resolves `UserRepositoryImpl` at runtime. Koin resolves across Gradle modules as long as every Koin module is loaded: the feature only needs to see the interface type. You don't need an extra entry point interface or component per feature.

With the Compiler Plugin, a feature module has no `startKoin` of its own, so its dependencies are checked when `:app` compiles and assembles the full graph. See [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety).

## Dynamic feature modules

With Play Feature Delivery, the base app can't reference code from a dynamic feature module: the dependency goes from the feature to the app. Load the feature Koin module from the feature itself, in its entry Activity, with `loadKoinModules()`:

```kotlin
// Dynamic feature module ":feature:premium"
internal object PremiumKoin {
    private val modules = lazy { loadKoinModules(premiumModule) }
    fun load() = modules.value
}

class PremiumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        PremiumKoin.load()
        super.onCreate(savedInstanceState)
    }
}
```

The `lazy` makes sure the module loads once per process, before `super.onCreate()`. Why both matter, and how to unload, is covered in [Starting Koin on Android](/docs/reference/koin-android/start#loading-modules-after-start).

The Compiler Plugin can't see a module loaded this way. Types used by the base app that only come from such a module must be marked `@Provided` (see [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety)).

## Testing feature modules

Test each feature module on its own, with fakes for what other modules provide. With the Compiler Plugin, the full graph is checked at build time. Without it, check each module with `verify()` (see [Verifying your Configuration](/docs/reference/koin-test/verify)). Unit tests with Koin are covered in [Unit Testing](/docs/reference/koin-test/testing), and on-device tests in [Instrumented Testing](/docs/reference/koin-android/instrumented-testing).

## Related

- [Modules](/docs/reference/koin-core/modules): `includes()`, module visibility and `@Configuration` discovery.
- [Starting Koin on Android](/docs/reference/koin-android/start): `startKoin` and loading modules at runtime.
- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): how the graph is checked across Gradle modules.
- [Lazy Modules](/docs/reference/koin-core/lazy-modules): load feature modules in the background at startup.
- [Android Library Integration](/docs/reference/koin-android/binding-patterns): data, domain and UI layers.
