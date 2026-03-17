---
title: Isolated Context
---

# Isolated Context in Compose

Koin's isolated context allows you to run a separate Koin instance that doesn't interfere with the host application's Koin configuration. This is essential for SDKs, libraries, and white-label applications.

## Use Cases

- **SDK Development** - Your SDK has its own dependencies without affecting the host app
- **White-Label Apps** - Multiple app variants with different configurations
- **Testing** - Isolated test configurations
- **Feature Modules** - Self-contained feature modules with their own DI

## Creating an Isolated Context

### Define the Context Holder

Create an object to hold your isolated Koin instance:

```kotlin
object MySDKKoinContext {
    val koinApp = koinApplication {
        // Your SDK modules
        modules(
            sdkCoreModule,
            sdkNetworkModule,
            sdkRepositoryModule
        )
    }

    // Convenience accessor
    val koin: Koin get() = koinApp.koin
}
```

### SDK Module Example

```kotlin
val sdkCoreModule = module {
    single<SDKConfiguration>()
    single<SDKLogger>()
}

val sdkNetworkModule = module {
    single<SDKApiClient>()
    single<SDKAuthManager>()
}

val sdkRepositoryModule = module {
    single<SDKDataRepository>()
}
```

## Using with Compose

### KoinIsolatedContext

Wrap your SDK's Compose UI with `KoinIsolatedContext`:

```kotlin
@Composable
fun MySDKScreen() {
    KoinIsolatedContext(context = MySDKKoinContext.koinApp) {
        // All Koin APIs use the isolated context
        SDKContent()
    }
}

@Composable
private fun SDKContent() {
    // These resolve from the isolated context
    val config = koinInject<SDKConfiguration>()
    val viewModel = koinViewModel<SDKViewModel>()

    Column {
        Text("SDK Version: ${config.version}")
        SDKFeatureScreen(viewModel)
    }
}
```

### Nested Contexts

You can nest isolated contexts:

```kotlin
@Composable
fun HostApp() {
    // Host app's Koin context (from startKoin)
    val hostService = koinInject<HostService>()

    Column {
        Text("Host App")

        // SDK uses its own isolated context
        KoinIsolatedContext(context = MySDKKoinContext.koinApp) {
            MySDKScreen()
        }

        // Back to host context
        AnotherHostScreen()
    }
}
```

## Lifecycle Management

### Manual Initialization

Initialize your SDK context when needed:

```kotlin
object MySDK {
    private var _koinContext: KoinApplication? = null
    val koinContext: KoinApplication
        get() = _koinContext ?: error("SDK not initialized")

    fun initialize(config: SDKConfig) {
        _koinContext = koinApplication {
            modules(
                module {
                    single { config }
                },
                sdkCoreModule,
                sdkNetworkModule
            )
        }
    }

    fun shutdown() {
        _koinContext?.close()
        _koinContext = null
    }
}
```

### Usage with Manual Lifecycle

```kotlin
// Host app initializes SDK
class HostApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Host app's Koin
        startKoin {
            androidContext(this@HostApplication)
            modules(hostAppModule)
        }

        // SDK's isolated Koin
        MySDK.initialize(SDKConfig(apiKey = "xxx"))
    }

    override fun onTerminate() {
        super.onTerminate()
        MySDK.shutdown()
    }
}

// In Compose
@Composable
fun SDKFeature() {
    KoinIsolatedContext(context = MySDK.koinContext) {
        SDKScreen()
    }
}
```

## Accessing Both Contexts

Sometimes you need to access both host and SDK dependencies:

```kotlin
@Composable
fun BridgeScreen() {
    // Get from host context (outside KoinIsolatedContext)
    val hostAnalytics = koinInject<HostAnalytics>()

    KoinIsolatedContext(context = MySDKKoinContext.koinApp) {
        // Get from SDK context (inside KoinIsolatedContext)
        val sdkService = koinInject<SDKService>()

        // Use both
        SDKFeature(
            service = sdkService,
            onEvent = { hostAnalytics.track(it) }
        )
    }
}
```

## Complete SDK Example

```kotlin
// SDK public API
object PaymentSDK {
    private lateinit var koinApp: KoinApplication

    fun initialize(config: PaymentConfig) {
        koinApp = koinApplication {
            modules(
                module { single { config } },
                paymentCoreModule,
                paymentUIModule
            )
        }
    }

    @Composable
    fun PaymentScreen(
        amount: Double,
        onSuccess: (PaymentResult) -> Unit,
        onCancel: () -> Unit
    ) {
        KoinIsolatedContext(context = koinApp) {
            PaymentFlow(
                amount = amount,
                onSuccess = onSuccess,
                onCancel = onCancel
            )
        }
    }
}

// SDK internal modules
private val paymentCoreModule = module {
    single<PaymentProcessor>()
    single<PaymentValidator>()
}

private val paymentUIModule = module {
    viewModel<PaymentViewModel>()
}

// SDK internal UI
@Composable
private fun PaymentFlow(
    amount: Double,
    onSuccess: (PaymentResult) -> Unit,
    onCancel: () -> Unit
) {
    val viewModel = koinViewModel<PaymentViewModel>()

    PaymentUI(
        amount = amount,
        state = viewModel.state,
        onPay = { viewModel.processPayment(amount, onSuccess) },
        onCancel = onCancel
    )
}

// Host app usage
@Composable
fun CheckoutScreen() {
    var showPayment by remember { mutableStateOf(false) }

    if (showPayment) {
        PaymentSDK.PaymentScreen(
            amount = 99.99,
            onSuccess = { result ->
                showPayment = false
                // Handle success
            },
            onCancel = { showPayment = false }
        )
    } else {
        Button(onClick = { showPayment = true }) {
            Text("Pay Now")
        }
    }
}
```

## Best Practices

1. **Initialize early** - Set up isolated context before Compose renders

2. **Clean up resources** - Call `close()` on the KoinApplication when done

3. **Don't share instances** - Keep SDK and host dependencies separate

4. **Use interface boundaries** - Communicate via callbacks or interfaces, not shared Koin instances

5. **Document initialization** - Make SDK setup requirements clear to host app developers

## Next Steps

- **[Compose Overview](/docs/reference/koin-compose/compose)** - Basic Compose setup
- **[Context Isolation](/docs/reference/koin-core/context-isolation)** - Core isolation concepts
- **[Scopes](/docs/reference/koin-compose/compose-scopes)** - Scope management in Compose
