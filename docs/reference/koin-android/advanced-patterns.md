---
title: Advanced Android Patterns
---

This guide covers Android-specific advanced dependency injection patterns with Koin.

:::info
For platform-agnostic patterns (collections, decorator, generic types, circular dependencies), see [Definitions](/docs/reference/koin-core/definitions) and [Modules](/docs/reference/koin-core/modules).
:::

## Android Context in Singletons

### Avoiding Activity Leaks

```kotlin
// ❌ Bad - Activity leaks through singleton
module {
    single { SomeService(get<Activity>()) }
}

// ✅ Good - Use Application context
module {
    single { SomeService(androidContext()) }
}

// ✅ Good - Use scoped for Activity-bound dependencies
module {
    activityScope {
        scoped { ActivityBoundService() }
    }
}
```

### Context Types

```kotlin
module {
    // Application context - safe for singletons
    single { DatabaseHelper(androidContext()) }

    // Application instance
    single { AppConfig(androidApplication()) }
}
```

## Conditional Bindings with BuildConfig

### Build Variant

```kotlin
fun createLogger(): Logger =
    if (BuildConfig.DEBUG) DebugLogger() else ReleaseLogger()

val loggingModule = module {
    single { create(::createLogger) }
}
```

Or with Annotations:

```kotlin
@Module
class LoggingModule {
    @Single
    fun provideLogger(): Logger =
        if (BuildConfig.DEBUG) DebugLogger() else ReleaseLogger()
}
```

### Analytics Toggle

```kotlin
fun createAnalyticsService(): AnalyticsService =
    if (BuildConfig.ENABLE_ANALYTICS) GoogleAnalytics() else NoOpAnalytics()

val analyticsModule = module {
    single { create(::createAnalyticsService) }
}
```

### Feature Flags

```kotlin
@Singleton
class PaymentProcessor(
    private val featureFlags: FeatureFlagService,
    private val newProcessor: NewPaymentProcessor,
    private val legacyProcessor: LegacyPaymentProcessor
) {
    fun process(amount: Double) {
        if (featureFlags.isEnabled("new_payment_flow")) {
            newProcessor.process(amount)
        } else {
            legacyProcessor.process(amount)
        }
    }
}
```

## Android Dialog Provider

Create factories for Android UI components:

```kotlin
@Factory
class DialogProvider(private val context: Context) {

    fun createConfirmDialog(title: String, onConfirm: () -> Unit): AlertDialog =
        AlertDialog.Builder(context)
            .setTitle(title)
            .setPositiveButton("OK") { _, _ -> onConfirm() }
            .create()

    fun createErrorDialog(message: String): AlertDialog =
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
}

class MainActivity : AppCompatActivity() {
    private val dialogProvider: DialogProvider by inject()

    fun showConfirmation() {
        dialogProvider.createConfirmDialog("Confirm") { /* action */ }.show()
    }
}
```

## Hierarchical Scopes

Link Android scopes for shared access:

```kotlin
val appModule = module {
    single { Database() }

    scope(named("session")) {
        scoped { UserSession() }
    }

    scope(named("shopping")) {
        scoped { ShoppingCart(get()) }
    }
}

// Create and link scopes
val sessionScope = getKoin().createScope("user_session", named("session"))
val shoppingScope = getKoin().createScope("cart", named("shopping"))
shoppingScope.linkTo(sessionScope)

// Shopping cart can access UserSession from linked scope
val cart = shoppingScope.get<ShoppingCart>()
```

## Dynamic Feature Registry

Build collections based on configuration:

```kotlin
@Singleton
class FeatureRegistry(private val config: AppConfig) : KoinComponent {

    fun getEnabledFeatures(): List<Feature> {
        return config.enabledFeatures.mapNotNull { name ->
            getKoin().getOrNull<Feature>(named(name))
        }
    }
}
```

## Common Android Pitfalls

### Hidden Circular Calls

```kotlin
// ⚠️ Lazy hides the cycle but infinite loop at runtime
class ServiceA : KoinComponent {
    private val serviceB: ServiceB by inject()
    fun doA() { serviceB.doB() }
}

class ServiceB : KoinComponent {
    private val serviceA: ServiceA by inject()
    fun doB() { serviceA.doA() }  // Infinite loop!
}
```

### ViewModel Scope Confusion

```kotlin
// ❌ Bad - ViewModel in activity scope loses state on rotation
module {
    activityScope {
        scoped { UserViewModel(get()) }
    }
}

// ✅ Good - Use viewModel for proper lifecycle
module {
    viewModel { UserViewModel(get()) }
}
```

### Injecting Activity in Singleton

```kotlin
// ❌ Memory leak - Activity reference in singleton
@Singleton
class ImageLoader(private val activity: Activity)

// ✅ Use Application context
@Singleton
class ImageLoader(private val context: Context)  // Application context via androidContext()
```

## Next Steps

- **[Android Scopes](/docs/reference/koin-android/scope)** - Lifecycle-aware scoping
- **[Multi-Module Apps](/docs/reference/koin-android/multi-module)** - Organizing Android modules
- **[Best Practices](/docs/reference/koin-android/best-practices)** - Memory management and migration
