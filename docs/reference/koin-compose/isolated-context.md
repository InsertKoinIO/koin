---
title: Isolated Context in Compose
description: Provide an isolated Koin instance to a Compose UI tree with KoinIsolatedContext, for SDKs and libraries that ship their own screens.
---

This page shows how to make Compose functions resolve from an isolated Koin instance with `KoinIsolatedContext`. Use it when you build an SDK or a library that ships Composables and must not share the host app's Koin container. What an isolated context is, and how to create one, is explained in [Context Isolation](/docs/reference/koin-core/context-isolation).

## KoinIsolatedContext

Keep the isolated `KoinApplication` in a static holder, as described in [Context Isolation](/docs/reference/koin-core/context-isolation):

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
object PaymentSdkKoin {
    val koinApp = koinApplication {
        modules(paymentModule)
    }
}

internal val paymentModule = module {
    single<PaymentProcessor>()
    viewModel<PaymentViewModel>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.plugin.module.dsl.koinApplication

@Module
@ComponentScan("com.example.paymentsdk")
internal class PaymentModule

@KoinApplication(modules = [PaymentModule::class])
internal object PaymentSdkApp

object PaymentSdkKoin {
    val koinApp = koinApplication<PaymentSdkApp>()
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
object PaymentSdkKoin {
    val koinApp = koinApplication {
        modules(paymentModule)
    }
}

internal val paymentModule = module {
    singleOf(::PaymentProcessor)
    viewModelOf(::PaymentViewModel)
}
```

</TabItem>
</Tabs>

Wrap the SDK UI with `KoinIsolatedContext`:

```kotlin
import org.koin.compose.KoinIsolatedContext

@Composable
fun PaymentScreen(amount: Double, onDone: (PaymentResult) -> Unit) {
    KoinIsolatedContext(context = PaymentSdkKoin.koinApp) {
        PaymentFlow(amount, onDone)
    }
}

@Composable
private fun PaymentFlow(amount: Double, onDone: (PaymentResult) -> Unit) {
    val viewModel = koinViewModel<PaymentViewModel>() // resolved from the SDK's Koin
    // ...
}
```

Inside the block, `koinInject()`, `koinViewModel()`, `getKoin()`, `currentKoinScope()` and `rememberKoinModules()` use the isolated instance. Outside the block, the host app keeps its own Koin.

## Host and SDK in the same screen

Resolve host dependencies outside the block, and SDK dependencies inside:

```kotlin
@Composable
fun CheckoutScreen() {
    val analytics = koinInject<HostAnalytics>() // host app's Koin

    KoinIsolatedContext(context = PaymentSdkKoin.koinApp) {
        val processor = koinInject<PaymentProcessor>() // SDK's Koin
        PaymentButton(processor, onEvent = { analytics.track(it) })
    }
}
```

The two containers don't see each other. Pass values across the boundary with parameters and callbacks.

## Initialization and shutdown

When the SDK needs configuration from the host, create the `KoinApplication` in an `initialize` function and close it on shutdown:

```kotlin
object PaymentSdk {
    private var koinApp: KoinApplication? = null

    fun initialize(config: PaymentConfig) {
        koinApp = koinApplication {
            modules(module { single { config } }, paymentModule)
        }
    }

    fun shutdown() {
        koinApp?.close()
        koinApp = null
    }

    @Composable
    fun PaymentScreen(amount: Double, onDone: (PaymentResult) -> Unit) {
        val app = koinApp ?: error("PaymentSdk.initialize() was not called")
        KoinIsolatedContext(context = app) {
            PaymentFlow(amount, onDone)
        }
    }
}
```

The host calls `PaymentSdk.initialize(...)` once, for example in `Application.onCreate()`, before any SDK screen is shown.

## Limits

- **KoinNavigationScope**: it creates its scope in the global Koin instance, so it does not follow `KoinIsolatedContext`. Use `KoinScope` inside the isolated block instead (see [Scopes in Compose](/docs/reference/koin-compose/compose-scopes)).
- **Code outside Compose**: SDK classes receive their dependencies through their constructors, from the isolated instance. An SDK entry point outside Compose (a `Service`, a callback called by the host) that must retrieve dependencies itself uses a `KoinComponent` that overrides `getKoin()`. See [Context Isolation](/docs/reference/koin-core/context-isolation).
- **Lifecycle**: `KoinIsolatedContext` does not start or stop the instance. You own its lifetime.

## Related

- [Context Isolation](/docs/reference/koin-core/context-isolation): create an isolated Koin instance and a custom `KoinComponent`.
- [Koin for Compose](/docs/reference/koin-compose/compose): `KoinApplication` and `koinInject()`.
- [Scopes in Compose](/docs/reference/koin-compose/compose-scopes): scopes inside an isolated block.
- [Isolated Context in Ktor](/docs/reference/koin-ktor/ktor-isolated): the same concept for a Ktor server.
