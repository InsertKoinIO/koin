---
title: Context Isolation
description: Run a private Koin container inside an SDK or library with koinApplication, so it never conflicts with the host app's Koin instance.
---

An isolated context is a Koin container that is not registered in the global context. This page shows how to create one for an SDK or a library, how to make your classes resolve from it, and how to test it. It assumes you know [how to start Koin](/docs/reference/koin-core/starting-koin).

## Why isolate

`startKoin` registers one container in the global context (`GlobalContext`). Every `KoinComponent` and every `by inject()` reads from it, and a second `startKoin` call fails with `KoinApplicationAlreadyStartedException`.

If your library calls `startKoin`, it fights with the host application for that single slot, and its definitions can override the app's ones. A library should create its own container with `koinApplication` instead and keep it private.

## Creating an isolated context

`koinApplication` builds a container with the same DSL as `startKoin`, without registering it globally. Keep it in an object of your SDK:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

internal val sdkModule = module {
    single<SdkRepository>()
    single<SdkService>()
}

internal object SdkKoinContext {
    val koinApp = koinApplication {
        modules(sdkModule)
    }
    val koin = koinApp.koin
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.plugin.module.dsl.koinApplication

@Module
@ComponentScan("com.example.sdk")
internal class SdkModule

@KoinApplication(modules = [SdkModule::class])
internal object SdkApplication

internal object SdkKoinContext {
    val koinApp = koinApplication<SdkApplication>()
    val koin = koinApp.koin
}
```

`koinApplication<T>()` loads the modules of the `@KoinApplication` class, like `startKoin<T>()`, but does not register the container globally. List the SDK modules explicitly, so the SDK container does not pick up `@Configuration` modules from the host app.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.core.module.dsl.*

internal val sdkModule = module {
    singleOf(::SdkRepository)
    singleOf(::SdkService)
}

internal object SdkKoinContext {
    val koinApp = koinApplication {
        modules(sdkModule)
    }
    val koin = koinApp.koin
}
```

</TabItem>
</Tabs>

The `object` creates the container on first access. To control when it starts and stops (an `init()` and `shutdown()` in your SDK API), create it in a function and call `koinApp.close()` on shutdown.

## Resolving from the isolated context

`KoinComponent` reads from the global context by default. Override `getKoin()` in an internal interface, and use that interface in your SDK classes:

```kotlin
internal interface SdkKoinComponent : KoinComponent {
    override fun getKoin(): Koin = SdkKoinContext.koin
}

class SdkClient : SdkKoinComponent {
    // resolved from SdkKoinContext, never from the host app
    private val service: SdkService by inject()
}
```

Classes created by Koin get their dependencies through the constructor, from the container that creates them. Only the entry points of your SDK (the classes the host app instantiates) need `SdkKoinComponent`.

## Testing

In a test, point `getKoin()` at the isolated context and load test modules into it:

```kotlin
class SdkClientTest : KoinTest {

    private val testModule = module {
        single<SdkService> { FakeSdkService() }
    }

    override fun getKoin(): Koin = SdkKoinContext.koin

    @Before
    fun setUp() {
        getKoin().loadModules(listOf(testModule))
    }

    @After
    fun tearDown() {
        getKoin().unloadModules(listOf(testModule))
    }
}
```

`testModule` is loaded after `sdkModule`, so its `SdkService` overrides the real one. More on tests in [Testing](/docs/reference/koin-test/testing).

## Platform support

Compose and Ktor have their own APIs to use an isolated context:

- **Compose**: `KoinIsolatedContext` provides an isolated container to a composable tree. See [Isolated Context in Compose](/docs/reference/koin-compose/isolated-context).
- **Ktor**: the `KoinIsolated` plugin runs a server with its own container. See [Isolated Context in Ktor](/docs/reference/koin-ktor/ktor-isolated).

## Limits

- **No global helpers**: top-level functions that use the global context, such as `GlobalContext.get()`, `KoinPlatform.getKoin()` or the default Android `by inject()` in an Activity, do not see the isolated container. Use your `SdkKoinComponent` instead.
- **Lifecycle is yours**: Koin does not close an isolated context. Call `koinApp.close()` when your SDK shuts down.
- **No sharing by default**: the isolated container does not see the host app's definitions. Pass what you need from the host through your SDK API, for example in `init(context)`.

## Related

- [Starting Koin](/docs/reference/koin-core/starting-koin): `startKoin`, `koinApplication` and the application DSL.
- [Koin Component](/docs/reference/koin-core/koin-component): `KoinComponent` and `getKoin()`.
- [Isolated Context in Compose](/docs/reference/koin-compose/isolated-context): `KoinIsolatedContext`.
- [Isolated Context in Ktor](/docs/reference/koin-ktor/ktor-isolated): the `KoinIsolated` plugin.
- [Testing](/docs/reference/koin-test/testing): `KoinTest` and test modules.
