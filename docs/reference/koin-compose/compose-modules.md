---
title: Dynamic Modules
---

# Dynamic Modules in Compose

Koin provides APIs to dynamically load and unload modules tied to Composable lifecycle. This is useful for feature modules, lazy loading, and on-demand dependencies.

## rememberKoinModules

Load Koin modules when a Composable enters composition:

```kotlin
val featureModule = module {
    factory<FeatureRepository>()
    viewModel<FeatureViewModel>()
}

@Composable
fun FeatureScreen() {
    // Load module when this Composable enters composition
    rememberKoinModules(featureModule)

    val viewModel = koinViewModel<FeatureViewModel>()
}
```

### Multiple Modules

```kotlin
@Composable
fun FeatureScreen() {
    rememberKoinModules(
        featureDataModule,
        featureDomainModule,
        featureUiModule
    )
}
```

### Unloading Modules

Control when modules are unloaded:

```kotlin
@Composable
fun FeatureScreen() {
    rememberKoinModules(
        featureModule,
        unloadOnForgotten = true,  // Unload when Composable leaves
        unloadOnAbandoned = true   // Unload if composition fails
    )
}
```

| Option | When Triggered |
|--------|----------------|
| `unloadOnForgotten` | Composable removed from composition |
| `unloadOnAbandoned` | Composition fails or is abandoned |

## Use Cases

### Feature Modules

Load feature-specific dependencies on demand:

```kotlin
// Feature module in separate Gradle module
val checkoutModule = module {
    factory<PaymentProcessor>()
    factory<CheckoutRepository>()
    viewModel<CheckoutViewModel>()
}

@Composable
fun CheckoutScreen() {
    rememberKoinModules(checkoutModule, unloadOnForgotten = true)

    val viewModel = koinViewModel<CheckoutViewModel>()
    CheckoutContent(viewModel)
}
```

### Lazy Feature Loading

Combine with navigation for lazy feature loading:

```kotlin
NavHost(navController, startDestination = "home") {
    composable("home") {
        HomeScreen()  // No extra modules needed
    }
    composable("checkout") {
        // Checkout module loaded only when navigating here
        rememberKoinModules(checkoutModule, unloadOnForgotten = true)
        CheckoutScreen()
    }
    composable("profile") {
        // Profile module loaded only when navigating here
        rememberKoinModules(profileModule, unloadOnForgotten = true)
        ProfileScreen()
    }
}
```

### Debug/Preview Modules

Swap implementations for previews:

```kotlin
val debugModule = module {
    single<ApiClient> { MockApiClient() }
}

@Preview
@Composable
fun FeatureScreenPreview() {
    rememberKoinModules(debugModule)
    FeatureScreen()
}
```

### Conditional Modules

Load modules based on conditions:

```kotlin
@Composable
fun App(isDebug: Boolean) {
    if (isDebug) {
        rememberKoinModules(debugModule)
    }

    MainScreen()
}
```

## With Lazy Modules

Combine with Koin's lazy module loading for better performance:

```kotlin
val featureModule = lazyModule {
    // Definitions parsed lazily when module is loaded
    factory<HeavyService>()
    viewModel<FeatureViewModel>()
}

@Composable
fun FeatureScreen() {
    rememberKoinModules(featureModule, unloadOnForgotten = true)

    val viewModel = koinViewModel<FeatureViewModel>()
}
```

## Best Practices

1. **Use `unloadOnForgotten = true`** - prevents memory leaks
   ```kotlin
   rememberKoinModules(featureModule, unloadOnForgotten = true)
   ```

2. **One module per feature** - keep modules focused and independent

3. **Combine with lazy modules** - for large apps with many features
   ```kotlin
   val featureModule = lazyModule { /* ... */ }
   ```

4. **Load at navigation level** - load modules in NavHost composables

5. **Avoid circular dependencies** - feature modules should not depend on each other

## Next Steps

- **[Scopes in Compose](/docs/reference/koin-compose/compose-scopes)** - Scope APIs
- **[Compose Overview](/docs/reference/koin-compose/compose)** - Setup and basic injection
- **[Isolated Context](/docs/reference/koin-compose/isolated-context)** - SDK isolation
