---
title: KMP Sharing Patterns
description: Give each Kotlin Multiplatform target its own implementations with expect/actual modules, expect/actual classes, or interfaces bound per platform.
---

This page covers how to declare definitions that differ per platform in a Kotlin Multiplatform project: a platform module with `expect`/`actual`, an `expect`/`actual` class, and an interface with one implementation per platform. It assumes your project is set up as in [KMP Setup](/docs/reference/koin-core/kmp-setup). For the Kotlin rules of `expect`/`actual`, see the [Kotlin documentation](https://kotlinlang.org/docs/multiplatform/multiplatform-expect-actual.html).

## Organizing shared modules

Put everything that doesn't depend on a platform in `commonMain` modules, split by layer or by feature, and assemble them with `includes()`:

```kotlin
// commonMain
val dataModule = module {
    single<ApiClient>()
    single<UserRepository>()
}

val domainModule = module {
    factory<GetUserUseCase>()
}

val appModule = module {
    includes(dataModule, domainModule, platformModule())
}
```

Module composition is described in [Modules](/docs/reference/koin-core/modules). The rest of this page is about `platformModule()`: the part that differs per platform.

## Platform module

Declare the module in `commonMain` with `expect`, and write one `actual` per platform. Each platform module declares its own classes:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
// commonMain
interface PlatformComponent {
    fun getInfo(): String
}

expect fun platformModule(): Module
```

```kotlin
// androidMain
class AndroidComponent(private val context: Context) : PlatformComponent {
    override fun getInfo() = "Android ${context.packageName}"
}

actual fun platformModule(): Module = module {
    single<AndroidComponent>() bind PlatformComponent::class
}
```

```kotlin
// iosMain
class IOSComponent : PlatformComponent {
    override fun getInfo() = "iOS"
}

actual fun platformModule(): Module = module {
    single<IOSComponent>() bind PlatformComponent::class
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
// commonMain
interface PlatformComponent {
    fun getInfo(): String
}

@Module
expect class PlatformModule
```

```kotlin
// androidMain
@Module
@ComponentScan
actual class PlatformModule

@Singleton
class AndroidComponent(private val context: Context) : PlatformComponent {
    override fun getInfo() = "Android ${context.packageName}"
}
```

```kotlin
// iosMain
@Module
@ComponentScan
actual class PlatformModule

@Singleton
class IOSComponent : PlatformComponent {
    override fun getInfo() = "iOS"
}
```

Each `actual` module scans its own source set, so it only finds the classes of that platform. `@Singleton` binds the class to its supertypes, so `PlatformComponent` resolves to the platform class. Include `PlatformModule` from a common module with `@Module(includes = [PlatformModule::class])`.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
// commonMain
interface PlatformComponent {
    fun getInfo(): String
}

expect fun platformModule(): Module
```

```kotlin
// androidMain
actual fun platformModule(): Module = module {
    singleOf(::AndroidComponent) bind PlatformComponent::class
}
```

```kotlin
// iosMain
actual fun platformModule(): Module = module {
    singleOf(::IOSComponent) bind PlatformComponent::class
}
```

</TabItem>
</Tabs>

Use this pattern when platform implementations need different constructor parameters. Common code depends on the `PlatformComponent` interface only, and each platform is free to inject what it needs (here, `Context` on Android). On Android, `Context` resolves once `androidContext()` is set at start (see [KMP Setup](/docs/reference/koin-core/kmp-setup)).

### Platform libraries

Platform drivers and engines (SQLDelight, Ktor client engines, settings) are built with a function call. Declare them in the platform module with a lambda:

```kotlin
// androidMain
actual fun platformModule(): Module = module {
    single<SqlDriver> { AndroidSqliteDriver(AppDatabase.Schema, androidContext(), "app.db") }
    single<HttpClientEngine> { OkHttp.create() }
}

// iosMain
actual fun platformModule(): Module = module {
    single<SqlDriver> { NativeSqliteDriver(AppDatabase.Schema, "app.db") }
    single<HttpClientEngine> { Darwin.create() }
}
```

The common module then builds on them without knowing the platform: `single { AppDatabase(get()) }`, `single { HttpClient(get<HttpClientEngine>()) }`. For other ways to declare classes you don't own, see [Definitions](/docs/reference/koin-core/definitions).

## expect/actual class

When the class has the same constructor on every platform, declare it once in common code and let each platform implement it:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
// commonMain
expect class NativeComponent() {
    fun getInfo(): String
}

val nativeComponentModule = module {
    single<NativeComponent>()
}
```

```kotlin
// androidMain
actual class NativeComponent actual constructor() {
    actual fun getInfo() = "Android NativeComponent"
}

// iosMain
actual class NativeComponent actual constructor() {
    actual fun getInfo() = "iOS NativeComponent"
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
// commonMain
@Module
@ComponentScan
class NativeComponentModule

@Singleton
expect class NativeComponent() {
    fun getInfo(): String
}
```

```kotlin
// androidMain
@Singleton
actual class NativeComponent actual constructor() {
    actual fun getInfo() = "Android NativeComponent"
}

// iosMain
@Singleton
actual class NativeComponent actual constructor() {
    actual fun getInfo() = "iOS NativeComponent"
}
```

You can also declare the `expect` class through a provider function of a common `@Module` class:

```kotlin
// commonMain
@Module
class NativeModule {
    @Factory
    fun nativeComponent(): NativeComponent = NativeComponent()
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
// commonMain
expect class NativeComponent() {
    fun getInfo(): String
}

val nativeComponentModule = module {
    singleOf(::NativeComponent)
}
```

The `actual` classes are the same as in the other tabs.

</TabItem>
</Tabs>

An `expect` class has one constructor contract for all platforms. If a platform needs another dependency (an Android `Context`, for example), you can't add it to that platform only. Use the [platform module](#platform-module) pattern with an interface instead, or a platform wrapper (see [KMP Platform Integration](/docs/reference/koin-mp/kmp)).

## Components on one platform only

A platform module can declare classes that exist on one platform and nowhere else. The other platforms keep an empty module:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
// androidMain
actual fun platformModule(): Module = module {
    single<NotificationHelper>()
}

// iosMain
actual fun platformModule(): Module = module { }
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
// androidMain
@Module
@ComponentScan("com.example.app.android")
actual class PlatformModule

// iosMain: nothing to declare
@Module
actual class PlatformModule
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
// androidMain
actual fun platformModule(): Module = module {
    singleOf(::NotificationHelper)
}

// iosMain
actual fun platformModule(): Module = module { }
```

</TabItem>
</Tabs>

Only Android code can then use `NotificationHelper`. Common code can't reference it, since the class does not exist in `commonMain`.

## Choosing a pattern

| Situation | Pattern |
|-----------|---------|
| Same constructor on every platform | `expect`/`actual` class |
| Different constructor parameters per platform | Interface in common code, implementation in the platform module |
| Platform library built with a function (driver, engine) | Lambda in the platform module |
| Class that exists on one platform only | Platform module, empty on other platforms |
| Android `Context` needed by shared code | Platform wrapper, see [KMP Platform Integration](/docs/reference/koin-mp/kmp) |

## Limits

- **Manual wiring**: if a provider function receives a Koin `Scope` and calls `scope.get()` itself, that call is dynamic and not covered by [compile-time safety](/docs/reference/koin-compiler/compile-safety). Prefer constructor parameters.
- **Tests**: shared modules can be tested in `commonTest`. See [Testing](/docs/reference/koin-test/testing).

## Related

- [KMP Setup](/docs/reference/koin-core/kmp-setup): dependencies and the shared `initKoin` function.
- [KMP Platform Integration](/docs/reference/koin-mp/kmp): Android context in shared code, Swift interop, desktop and web.
- [Modules](/docs/reference/koin-core/modules): `includes()`, `@Module`, `@ComponentScan`, `@Configuration`.
- [Definitions](/docs/reference/koin-core/definitions): binding, provider functions, classes you don't own.
- [ViewModel](/docs/reference/koin-core/viewmodel): ViewModels declared in `commonMain`.
