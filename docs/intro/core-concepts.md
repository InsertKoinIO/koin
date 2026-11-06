---
title: Core Concepts
description: The Koin mental model in five minutes. Definitions, modules, the container, injection, scopes, qualifiers and compile-time checks, with one example.
---

This page builds one small example step by step to explain the ideas behind every other page: definitions, modules, the Koin container, injection, scopes, qualifiers and compile-time checks. Each section is short and links to the page that holds the detail. It assumes you know what dependency injection is (see [What is Dependency Injection?](/docs/intro/what-is-dependency-injection)).

Code samples come in three tabs: Compiler Plugin DSL, Annotations, Constructor / Classic DSL. Pick yours, the choice applies to every page. If you are not sure which style to use, read [Choosing your style](/docs/intro/choosing-your-style).

## The example

Three plain Kotlin classes. Each one receives what it needs in its constructor and knows nothing about Koin:

```kotlin
class ApiService

class UserRepository(private val api: ApiService)

class UserViewModel(private val repository: UserRepository) : ViewModel()
```

Koin's job is to create `ApiService`, pass it to `UserRepository`, and pass that to `UserViewModel`, without you writing the wiring by hand.

## Definition

A **definition** tells Koin how to create one type and how long to keep the instance:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
single<ApiService>()
single<UserRepository>()
viewModel<UserViewModel>()
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class ApiService

@Singleton
class UserRepository(private val api: ApiService)

@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
singleOf(::ApiService)
singleOf(::UserRepository)
viewModelOf(::UserViewModel)
```

</TabItem>
</Tabs>

The keyword sets the lifetime. `single` keeps one instance for the whole container. `factory` creates a new instance on every request. `viewModel` follows the ViewModel lifecycle. Koin reads the constructor of `UserRepository`, sees it needs an `ApiService`, and resolves it from the container.

Detail: [Definitions](/docs/reference/koin-core/definitions).

## Module

A **module** groups definitions. You usually have one module per feature or layer:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val appModule = module {
    single<ApiService>()
    single<UserRepository>()
    viewModel<UserViewModel>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Module
@ComponentScan("com.example.app")
class AppModule
```

`@ComponentScan` collects every annotated class in the package `com.example.app` and its subpackages.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.dsl.module
import org.koin.core.module.dsl.*

val appModule = module {
    singleOf(::ApiService)
    singleOf(::UserRepository)
    viewModelOf(::UserViewModel)
}
```

</TabItem>
</Tabs>

A module is only a description. Nothing is created until Koin starts and someone asks for an instance. Modules can include other modules, so a large app is a tree of small modules.

Detail: [Modules](/docs/reference/koin-core/modules).

## The Koin container

`startKoin` creates the **container** (a `KoinApplication`) and loads your modules into it. You call it once, at the entry point of your app:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
fun main() {
    startKoin {
        modules(appModule)
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [AppModule::class])
class MyApp

fun main() {
    startKoin<MyApp>()
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
fun main() {
    startKoin {
        modules(appModule)
    }
}
```

</TabItem>
</Tabs>

On Android you call it in `Application.onCreate()` and pass the Android context with `androidContext(this)`. The container holds the definitions and the instances created so far. `stopKoin()` closes it and releases every `single`.

Detail: [Starting Koin](/docs/reference/koin-core/starting-koin), and [Start Koin on Android](/docs/reference/koin-android/start).

## Injection

There are two ways an object gets its dependencies.

**Constructor injection**: the dependency is a constructor parameter, and Koin passes it when it creates the object. `UserRepository` and `UserViewModel` in this example already use it. This is the default: the class stays plain Kotlin and you can test it by calling the constructor with fakes.

**Retrieving from the container**: some classes are created by a framework, not by Koin (an Android `Activity`, a Composable, a Ktor route). They ask the container with `get()` (now) or `inject()` (lazily, on first access):

```kotlin
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModel()
}
```

```kotlin
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserScreen(viewModel: UserViewModel = koinViewModel()) {
    ...
}
```

Retrieving is the same in every style. Keep it at these entry points: everything below them uses constructor injection.

Detail: [Retrieving Dependencies](/docs/reference/koin-core/injection), [Android Entry Points](/docs/reference/koin-android/entry-points), [Koin for Compose](/docs/reference/koin-compose/compose).

## The graph

Put together, the container holds this graph:

```text
startKoin
  └── appModule
        ├── single     ApiService
        ├── single     UserRepository  ── needs ── ApiService
        └── viewModel  UserViewModel   ── needs ── UserRepository

UserActivity ── by viewModel() ──> UserViewModel
```

When `UserActivity` asks for `UserViewModel`, Koin creates `ApiService` (once), then `UserRepository` (once), then a `UserViewModel` bound to the Activity's ViewModel lifecycle.

## Scope

A `single` lives as long as the container, a `factory` lives as long as the caller keeps it. A **scope** covers the space between: instances that live as long as something else, such as a user session, a screen or a request.

Say a `CartRepository` should exist only while a user is logged in:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class UserSession
class CartRepository(private val api: ApiService)

val sessionModule = module {
    scope<UserSession> {
        scoped<CartRepository>()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
class UserSession

@Scope(UserSession::class)
@Scoped
class CartRepository(private val api: ApiService)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
class UserSession
class CartRepository(private val api: ApiService)

val sessionModule = module {
    scope<UserSession> {
        scopedOf(::CartRepository)
    }
}
```

</TabItem>
</Tabs>

You open the scope when the session starts and close it when the session ends:

```kotlin
// inside a KoinComponent
val sessionScope = getKoin().createScope<UserSession>("session-42")
val cart: CartRepository = sessionScope.get()

sessionScope.close() // releases CartRepository
```

A scoped definition can use definitions from the root container (`ApiService` here). On Android, Koin provides ready-made scopes for Activities, Fragments and ViewModels, so you rarely create scopes by hand.

Detail: [Scopes](/docs/reference/koin-core/scopes), and [Android Scopes](/docs/reference/koin-android/scope).

## Qualifier

Koin finds a definition by its type. When two definitions provide the same type, a **qualifier** gives each one a name:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
interface NetworkClient

@Named("auth")
class AuthClient : NetworkClient

@Named("public")
class PublicClient : NetworkClient

class UserRepository(@Named("auth") private val client: NetworkClient)

val networkModule = module {
    single<AuthClient>() bind NetworkClient::class
    single<PublicClient>() bind NetworkClient::class
    single<UserRepository>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
@Named("auth")
class AuthClient : NetworkClient

@Singleton
@Named("public")
class PublicClient : NetworkClient

@Singleton
class UserRepository(@Named("auth") private val client: NetworkClient)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val networkModule = module {
    single<NetworkClient>(named("auth")) { AuthClient() }
    single<NetworkClient>(named("public")) { PublicClient() }
    single { UserRepository(get(named("auth"))) }
}
```

</TabItem>
</Tabs>

Detail: [Qualifiers](/docs/reference/koin-core/qualifiers).

## Compile-time checks

Without the Compiler Plugin, a missing definition shows up when the code runs: `get()` throws `NoDefinitionFoundException`. With the [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin), the same mistake fails the build. If you remove `single<ApiService>()` from `appModule`, compilation stops with:

```text
[Koin][KOIN-D001] Missing dependency: ApiService
  required by: UserRepository (parameter 'api')
  in module: ...
```

The plugin checks the full graph at each entry point (`startKoin`, `@KoinApplication`, Ktor's `install(Koin)`) and every `get()`, `inject()` or `koinViewModel()` call. With the Constructor or Classic DSL and no plugin, you get a similar check in a unit test with `verify()`.

Detail: [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety), [Verifying your Koin configuration](/docs/reference/koin-test/verify).

## Related

- [Choosing your style](/docs/intro/choosing-your-style): which declaration style to use.
- [Setup](/docs/setup): add Koin and the Compiler Plugin to your build.
- [Tutorials](/docs/quickstart/kotlin): build a complete app with these concepts.
- [Definitions](/docs/reference/koin-core/definitions): every definition type in detail.
- [Scopes](/docs/reference/koin-core/scopes): lifecycles beyond single and factory.
