---
title: Scopes in Compose
---

# Scopes in Compose

Koin provides several APIs to manage scopes within Compose applications, from simple composable-bound scopes to navigation-integrated scopes.

## KoinScope

Create a Koin scope tied to a Composable's lifecycle:

```kotlin
val featureModule = module {
    scope<FeatureScope> {
        scoped<FeatureCache>()
        scoped<FeatureRepository>()
    }
}

@Composable
fun FeatureScreen() {
    KoinScope(scopeOf<FeatureScope>()) {
        // All children can access scoped dependencies
        FeatureContent()
    }
}

@Composable
fun FeatureContent() {
    // Resolves from parent KoinScope
    val cache = koinInject<FeatureCache>()
}
```

The scope is automatically closed when the Composable leaves composition (on `onForgotten` or `onAbandoned`).

## KoinNavigationScope

Create a scope tied to a navigation back stack entry:

```kotlin
val appModule = module {
    // Define navigation-scoped dependencies
    navigationScope {
        scoped<ScreenRepository>()
        scoped<ScreenCache>()
        viewModel<ScreenViewModel>()
    }
}

// In your NavHost
NavHost(navController, startDestination = "home") {
    composable("detail/{id}") { backStackEntry ->
        KoinNavigationScope(backStackEntry) {
            DetailScreen()
        }
    }
}

@Composable
fun DetailScreen() {
    // Dependencies scoped to this navigation destination
    val repository = koinInject<ScreenRepository>()
    val viewModel = koinViewModel<ScreenViewModel>()
}
```

**Key characteristics:**
- Scope ID derived from `NavBackStackEntry.id`
- Scope closed only when navigation is abandoned (not on recomposition)
- Perfect for per-screen dependencies

:::info
Requires `koin-compose-viewmodel-navigation` package.
:::

### navigationScope DSL

Define navigation-scoped dependencies in your modules:

```kotlin
val appModule = module {
    // Dependencies scoped to navigation destinations
    navigationScope {
        scoped<ScreenRepository>()
        scoped<ScreenStateHolder>()
        viewModel<ScreenViewModel>()
    }
}
```

This creates a scope qualified by `NavBackStackEntry` for use with `KoinNavigationScope()`.

## UnboundKoinScope

Provide an externally-managed scope without lifecycle binding:

```kotlin
@Composable
fun MyFeature(externalScope: Scope) {
    UnboundKoinScope(scope = externalScope) {
        // Children can access the scope
        val service = koinInject<MyService>()
        FeatureContent()
    }
}
```

:::warning
**Delicate API** - The scope is NOT automatically closed. You must manage the scope lifecycle manually to prevent memory leaks.
:::

**Use cases:**
- Scopes managed by external systems
- Scopes shared across multiple composable trees
- When scope lifecycle doesn't match Composable lifecycle

```kotlin
@Composable
fun MyFeature(externalScope: Scope, onClose: () -> Unit) {
    UnboundKoinScope(scope = externalScope) {
        FeatureContent()

        // Manual cleanup when needed
        DisposableEffect(Unit) {
            onDispose { onClose() }
        }
    }
}
```

## currentKoinScope

Get the current Koin scope from composition:

```kotlin
@Composable
fun MyScreen() {
    val scope = currentKoinScope()

    // Use scope directly
    val service = scope.get<MyService>()
}
```

This retrieves the scope from `LocalKoinScopeContext`. It's the default scope used by `koinInject()`.

## rememberKoinScope

Remember a Koin scope across recompositions with automatic lifecycle management:

```kotlin
@Composable
fun FeatureScreen() {
    val scope = rememberKoinScope(scopeOf<FeatureScope>())

    // Use scope for injection
    val repository = scope.get<FeatureRepository>()

    // When FeatureScreen leaves composition, scope is closed
}
```

## Android-Specific Scopes

### KoinActivityScope

Provide Activity scope to composable hierarchy:

```kotlin
class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoinActivityScope {
                // All children access Activity's scope
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    // Resolves from Activity's scope
    val presenter = koinInject<ActivityPresenter>()
}
```

### KoinFragmentScope

Provide Fragment scope to composable hierarchy:

```kotlin
class MyFragment : Fragment(), AndroidScopeComponent {
    override val scope: Scope by fragmentScope()

    override fun onCreateView(...): View {
        return ComposeView(requireContext()).apply {
            setContent {
                KoinFragmentScope {
                    FragmentScreen()
                }
            }
        }
    }
}
```

### koinActivityInject

Inject from Activity scope within any Composable:

```kotlin
val appModule = module {
    scope<MainActivity> {
        scoped<SessionManager>()
    }
}

@Composable
fun DeepNestedScreen() {
    // Resolves from Activity's scope anywhere in the tree
    val sessionManager: SessionManager = koinActivityInject()
}
```

## Scope Comparison

| API | Lifecycle | Use Case |
|-----|-----------|----------|
| `KoinScope` | Composable | Custom scoped composables |
| `KoinNavigationScope` | NavBackStackEntry | Per-destination scopes |
| `UnboundKoinScope` | Manual | External scope providers |
| `KoinActivityScope` | Activity | Activity-wide dependencies |
| `KoinFragmentScope` | Fragment | Fragment-wide dependencies |

## Use Cases

### Per-Screen Navigation Scopes

Each screen gets its own scope:

```kotlin
val appModule = module {
    navigationScope {
        scoped<ScreenStateHolder>()
        viewModel<ScreenViewModel>()
    }
}

NavHost(navController, startDestination = "list") {
    composable("list") { entry ->
        KoinNavigationScope(entry) {
            ListScreen() // Has its own ScreenStateHolder
        }
    }
    composable("detail/{id}") { entry ->
        KoinNavigationScope(entry) {
            DetailScreen() // Has its own ScreenStateHolder
        }
    }
}
```

### Session-Scoped Data

Share data across screens within a session:

```kotlin
val sessionModule = module {
    scope<UserSession> {
        scoped { ShoppingCart() }
        scoped { UserPreferences() }
    }
}

@Composable
fun ShopApp() {
    KoinScope(scopeOf<UserSession>()) {
        NavHost(/*...*/) {
            composable("catalog") { CatalogScreen() }
            composable("cart") { CartScreen() }
        }
    }
}

@Composable
fun CartScreen() {
    // Same cart instance across all screens in session
    val cart = koinInject<ShoppingCart>()
}
```

### Shared ViewModel Scope

Share a ViewModel and its dependencies across related screens:

```kotlin
val appModule = module {
    scope<CheckoutFlow> {
        scoped<CheckoutState>()
        viewModel<CheckoutViewModel>()
    }
}

@Composable
fun CheckoutFlow() {
    KoinScope(scopeOf<CheckoutFlow>()) {
        NavHost(/*...*/) {
            composable("cart") { CartScreen() }
            composable("shipping") { ShippingScreen() }
            composable("payment") { PaymentScreen() }
            composable("confirmation") { ConfirmationScreen() }
        }
    }
}

// All screens share the same CheckoutViewModel instance
@Composable
fun CartScreen() {
    val viewModel = koinViewModel<CheckoutViewModel>()
}
```

## Best Practices

1. **Use `KoinNavigationScope` for per-screen dependencies** - automatic lifecycle with navigation

2. **Prefer managed scopes over `UnboundKoinScope`** - avoid manual cleanup

3. **Define navigation scopes in modules** - cleaner than inline scope creation
   ```kotlin
   module {
       navigationScope {
           scoped<MyRepository>()
       }
   }
   ```

4. **Use `KoinScope` for multi-screen flows** - checkout, onboarding, wizards

5. **Combine with ViewModel for complex state** - scope holds shared state, ViewModel handles UI logic

## Next Steps

- **[Dynamic Modules](/docs/reference/koin-compose/compose-modules)** - Load modules dynamically
- **[Compose Overview](/docs/reference/koin-compose/compose)** - Setup and basic injection
- **[Core Scopes](/docs/reference/koin-core/scopes)** - Scope concepts
