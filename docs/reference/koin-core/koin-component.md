---
title: KoinComponent
description: Use the KoinComponent interface to retrieve dependencies with get() and inject() in classes that Koin does not create.
---

`KoinComponent` is an interface that gives a class access to the Koin container, so it can call `get()` and `by inject()`. This page covers when you need it, how to use it, and why constructor injection is preferred. For the retrieval functions themselves, see [Injection](/docs/reference/koin-core/injection).

## When to use KoinComponent

Use `KoinComponent` for a class that Koin does not create and that has no Koin extension of its own:

- Entry points: a `main()` class, a CLI command, a class created by a framework or by reflection.
- Android classes without Koin extensions: `BroadcastReceiver`, `ContentProvider`, custom `View`. See [Android Entry Points](/docs/reference/koin-android/entry-points).
- Callbacks and listeners whose constructor you don't control.
- Legacy code you can't move to constructor injection yet.

You don't need it for:

- Classes declared in a module: repositories, services, use cases, ViewModels. Koin passes their dependencies through the constructor.
- `Activity`, `Fragment` and `Service`: Koin Android provides `by inject()` and `by viewModel()` on them directly.
- Composables: use `koinInject()`, see [Koin for Compose](/docs/reference/koin-compose/compose).

## Use KoinComponent

Implement the interface, then call `get()` or `by inject()`:

```kotlin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

class SyncCommand : KoinComponent {

    // lazy: resolved on first access
    private val syncService: SyncService by inject()

    // eager: resolved when SyncCommand is created
    private val logger: Logger = get()

    fun run() {
        logger.info("Starting sync")
        syncService.sync()
    }
}

fun main() {
    startKoin {
        modules(appModule)
    }
    SyncCommand().run()
}
```

The code is the same in every style: `KoinComponent` only retrieves instances, the definitions can be declared with any style.

`get()` and `inject()` are extension functions: import them from `org.koin.core.component`. They take a qualifier and a parameter block, like every retrieval function (see [Injection](/docs/reference/koin-core/injection#qualifiers-and-parameters-at-the-call-site)).

`KoinComponent` itself has one function, `getKoin()`, which returns the `Koin` instance. Use it for everything else:

```kotlin
class FeatureLoader : KoinComponent {

    fun optionalTracker(): Tracker? = getKoin().getOrNull()

    fun apiUrl(): String = getKoin().getProperty("server_url", "https://api.example.com")

    fun allPlugins(): List<Plugin> = getKoin().getAll()
}
```

Nullable retrieval and `getAll()` are covered in [Injection](/docs/reference/koin-core/injection). Properties are covered in [Starting Koin](/docs/reference/koin-core/starting-koin#properties).

### Custom Android View

A custom `View` is created by the layout inflater, so Koin can't pass constructor parameters. `KoinComponent` fits here:

```kotlin
class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs), KoinComponent {

    private val formatter: ChartFormatter by inject()

    fun setData(points: List<DataPoint>) {
        val formatted = formatter.format(points)
        // draw
    }
}
```

Use `by inject()` in a `View`: the layout editor creates views without a running Koin container, and an eager `get()` would fail there.

## Which container KoinComponent uses

By default, `getKoin()` returns the global container started with `startKoin`. If `startKoin` has not been called yet, `get()` throws `IllegalStateException` ("KoinApplication has not been started").

A library or SDK that runs its own isolated container overrides `getKoin()` to return it:

```kotlin
object SdkKoinContext {
    val koinApp = koinApplication { modules(sdkModule) }
}

abstract class SdkComponent : KoinComponent {
    override fun getKoin(): Koin = SdkKoinContext.koinApp.koin
}

class SdkClient : SdkComponent() {
    private val api: SdkApi by inject() // resolved from the SDK container
}
```

The full pattern is in [Context Isolation](/docs/reference/koin-core/context-isolation).

For a class that owns a scope, implement `KoinScopeComponent` instead: `get()` and `inject()` then resolve from that scope. See [Scopes](/docs/reference/koin-core/scopes).

## Why constructor injection is preferred

Compare the two versions of the same class:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
// depends on Koin, dependencies are hidden in the body
class UserService : KoinComponent {
    private val repository: UserRepository by inject()
}

// plain Kotlin class, dependencies are visible in the constructor
class UserService(private val repository: UserRepository)

val appModule = module {
    single<UserRepository>()
    single<UserService>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
// depends on Koin, dependencies are hidden in the body
class UserService : KoinComponent {
    private val repository: UserRepository by inject()
}

// plain Kotlin class, dependencies are visible in the constructor
@Singleton
class UserService(private val repository: UserRepository)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
// depends on Koin, dependencies are hidden in the body
class UserService : KoinComponent {
    private val repository: UserRepository by inject()
}

// plain Kotlin class, dependencies are visible in the constructor
class UserService(private val repository: UserRepository)

val appModule = module {
    singleOf(::UserRepository)
    singleOf(::UserService)
}
```

</TabItem>
</Tabs>

The constructor version is better in four ways:

- Visible dependencies: the constructor lists what the class needs. With `KoinComponent`, you have to read the body.
- Tests without Koin: you create `UserService(FakeUserRepository())` directly. The `KoinComponent` version needs a running container with a fake definition.
- Build-time checks: the Compiler Plugin verifies constructor dependencies of declared classes against the graph.
- No framework coupling: the class compiles and runs without Koin, which matters for shared code and libraries.

Keep `KoinComponent` at the edges. One entry point retrieves the top object, and everything below it uses constructor injection:

```kotlin
class SyncCommand : KoinComponent {
    private val syncService: SyncService by inject()
}

class SyncService(private val repository: UserRepository, private val api: ApiClient)
class UserRepository(private val database: Database)
```

## Testing a KoinComponent

A class that implements `KoinComponent` reads from the global container, so the test must start Koin with the definitions it needs, and stop it afterward:

```kotlin
class SyncCommandTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<SyncService> { FakeSyncService() }
            single<Logger> { TestLogger() }
        })
    }

    @Test
    fun `run calls sync`() {
        SyncCommand().run()
        // assert on FakeSyncService
    }
}
```

`KoinTestRule` starts Koin before each test and stops it after. Rules, mocking and module verification are covered in [Testing](/docs/reference/koin-test/testing).

## Related

- [Injection](/docs/reference/koin-core/injection): `get()`, `inject()`, nullable retrieval and `getAll()`.
- [Android Entry Points](/docs/reference/koin-android/entry-points): which Android classes need `KoinComponent` and which don't.
- [Context Isolation](/docs/reference/koin-core/context-isolation): point `KoinComponent` to an isolated container.
- [Scopes](/docs/reference/koin-core/scopes): `KoinScopeComponent` for classes that own a scope.
- [Testing](/docs/reference/koin-test/testing): test classes that use Koin.
