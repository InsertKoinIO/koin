---
title: Scopes
---

# Scopes

Scopes control the lifecycle of your dependencies. This guide covers how to define, create, and manage scopes.

## Understanding Scopes

| Scope Type | Lifecycle | Example |
|------------|-----------|---------|
| **Single** (Singleton) | App lifetime | Database, ApiClient |
| **Factory** | Per request | Presenters, Use Cases |
| **Scoped** | Per scope | Activity-bound, Session-bound |

## When to Use Scopes

Use scopes when you need:
- Instances that live longer than a factory but shorter than a singleton
- Shared state within a specific context (Activity, Fragment, Session)
- Automatic cleanup when a context ends

## Defining Scoped Definitions

### DSL

```kotlin
val appModule = module {
    // Scope for MyActivity
    scope<MyActivity> {
        scoped<Presenter>()
        scoped<Navigator>()
    }

    // Named scope
    scope(named("session")) {
        scoped<SessionData>()
        scoped<UserPreferences>()
    }
}
```

### Annotations

| Annotation | DSL Equivalent | Purpose |
|------------|----------------|---------|
| `@Scope` | `scope<T> { }` | Specify which scope a class belongs to |
| `@Scoped` | `scoped<T>()` | Define a scoped binding |

A scoped class needs both `@Scoped` and `@Scope`:

```kotlin
@Scope(MyActivityScope::class)
@Scoped
class Presenter(private val repository: UserRepository)

@Scope(MyActivityScope::class)
@Scoped
class Navigator
```

Or use scope archetype annotations for common Android scopes (no `@Scoped` needed):

```kotlin
// ViewModel scope
@ViewModelScope
class UserCache

// Activity scope
@ActivityScope
class ActivityPresenter

@ActivityRetainedScope
class RetainedPresenter

// Fragment scope
@FragmentScope
class FragmentPresenter
```

## Creating and Using Scopes

### Using Scopes with `use { }`

`Scope` implements `AutoCloseable`, so you can use Kotlin's `use { }` block for safe, automatic cleanup. This is the recommended approach for short-lived scopes (request handling, transactions, batch jobs, etc.):

```kotlin
getKoin().createScope("my_scope_id", named("session")).use { scope ->
    val sessionData: SessionData = scope.get()
    val prefs: UserPreferences = scope.get()
}
// scope is closed automatically, even on exceptions
```

### Manual Scope Management

For longer-lived scopes, you can manage the lifecycle manually:

```kotlin
val myScope = getKoin().createScope("my_scope_id", named("session"))

// Get instances from scope
val sessionData: SessionData = myScope.get()
val prefs: UserPreferences = myScope.get()

// Close when done
myScope.close()
```

### Android Activity Scope

```kotlin
class MyActivity : AppCompatActivity(), AndroidScopeComponent {
    // Automatically create and destroy Scope based on Activity Lifecycle
    override val scope: Scope by activityScope()

    // Scoped instances - created per Activity instance
    private val presenter: Presenter by inject()

    override fun onDestroy() {
        super.onDestroy()
        // Scope automatically closed
    }
}
```

### Android Fragment Scope

```kotlin
class MyFragment : Fragment(), AndroidScopeComponent {
    // Automatically create and destroy Scope based on Fragment Lifecycle
    override val scope: Scope by fragmentScope()

    private val presenter: Presenter by inject()
}
```

## Scope Types

### Type-Based Scope

```kotlin
scope<MyActivity> {
    scoped<ActivityPresenter>()
}
```

The scope is identified by the type `MyActivity`. This scope is only triggered by `MyActivity`, whereas `activityScope` is a generic one.

### Named Scope

```kotlin
scope(named("user_session")) {
    scoped<SessionManager>()
}
```

Use when the scope isn't tied to a specific type.

### Qualifier-Based Scope

```kotlin
scope(named<MyQualifier>()) {
    scoped<ScopedService>()
}
```

## Scope Archetypes

Koin provides dedicated DSL for common Android scope patterns. These archetypes simplify scope definition for ViewModel, Activity, and Fragment.

### ViewModel Scope

Define dependencies scoped to a ViewModel's lifecycle:

```kotlin
val appModule = module {
    viewModelScope {
        scoped<UserCache>()
        scoped<UserRepository>()
        viewModel<UserViewModel>()
    }
}
```

The ViewModel automatically gets access to its scoped dependencies:

```kotlin
class UserViewModel(
    private val cache: UserCache,      // Scoped to this ViewModel
    private val repository: UserRepository
) : ViewModel()
```

### Activity Scope

Define dependencies scoped to an Activity's lifecycle:

```kotlin
val appModule = module {
    activityScope {
        scoped<ActivityPresenter>()
        scoped<ActivityNavigator>()
    }
}
```

### Fragment Scope

Define dependencies scoped to a Fragment's lifecycle:

```kotlin
val appModule = module {
    fragmentScope {
        scoped<FragmentPresenter>()
    }
}
```

### Comparison

| Archetype | DSL | Annotation | Lifecycle |
|-----------|-----|------------|-----------|
| ViewModel | `viewModelScope { }` | `@ViewModelScope` | ViewModel cleared |
| Activity | `activityScope { }` | `@ActivityScope` | Activity destroyed |
| Activity Retained | `activityRetainedScope { }` | `@ActivityRetainedScope` | Activity finished |
| Fragment | `fragmentScope { }` | `@FragmentScope` | Fragment destroyed |

:::info
Scope archetypes are available in Koin 4.0+. They provide a cleaner syntax than manually defining `scope<T> { }` for common Android components.
:::

## Scope Linking

Link scopes to access parent scope definitions:

```kotlin
val appModule = module {
    // Activity scope
    scope<MainActivity> {
        scoped<ActivityData>()
    }

    // Fragment scope linked to Activity
    scope<UserFragment> {
        scoped<FragmentPresenter>()
    }
}
```

```kotlin
class UserFragment : Fragment(), AndroidScopeComponent {
    override val scope: Scope by fragmentScope()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Link to parent Activity scope
        scope.linkTo((requireActivity() as AndroidScopeComponent).scope)

        // Now can access both Fragment and Activity scoped instances
        val fragmentPresenter: FragmentPresenter by inject()
        val activityData: ActivityData by inject()  // From linked scope
    }
}
```

## Scope Source

Inject dependencies that are aware of their scope:

```kotlin
class Presenter(
    val scope: Scope  // Injected by Koin
) {
    fun clearScope() {
        scope.close()
    }
}

scope<MyActivity> {
    scoped { Presenter(get()) }  // Scope injected
}
```

## Scope Instance ID

Each scope instance has a unique ID:

```kotlin
// Create with explicit ID
val scope1 = getKoin().createScope("scope_1", named("session"))
val scope2 = getKoin().createScope("scope_2", named("session"))

// Different instances, same scope type
scope1.get<SessionData>() !== scope2.get<SessionData>()
```

## Accessing Scoped Instances

### From Within Scope

```kotlin
class MyActivity : AppCompatActivity(), AndroidScopeComponent {
    override val scope: Scope by activityScope()

    // Directly inject scoped instances
    private val presenter: Presenter by inject()
}
```

### From Outside Scope

```kotlin
// Get or create scope
val myScope = getKoin().getOrCreateScope("my_id", named("session"))

// Get instance
val session: SessionData = myScope.get()
```

### In Compose

```kotlin
@Composable
fun MyScreen() {
    // Create scope tied to Composable lifecycle
    val scope = rememberKoinScope(named("screen_scope"))

    // Get scoped instance
    val presenter: ScreenPresenter = scope.get()
}
```

## Scope Lifecycle

### Closing Scopes

`Scope` implements `AutoCloseable`. The recommended way to handle scope cleanup is with `use { }`:

```kotlin
getKoin().createScope("my_scope", named("session")).use { scope ->
    val data: SessionData = scope.get()
}
// All scoped instances released automatically, even on exceptions
```

When a scope closes:
1. All scoped instances are released
2. `onClose` callbacks are invoked
3. Scope becomes unusable

```kotlin
// Manual close is also supported
val scope = getKoin().createScope("my_scope", named("session"))
val data: SessionData = scope.get()
scope.close()  // SessionData instance released

// This throws an exception
// scope.get<SessionData>()  // Error: Scope is closed
```

### onClose Callback

```kotlin
scope(named("session")) {
    scoped {
        SessionData()
    } onClose {
        it?.cleanup()  // Called when scope closes
    }
}
```

## Common Patterns

### Session Scope

```kotlin
val appModule = module {
    scope(named("user_session")) {
        scoped { SessionManager() }
        scoped { UserPreferences(get()) }
        scoped { CartRepository(get()) }
    }
}

// Login
fun onLogin(userId: String) {
    val sessionScope = getKoin().createScope(userId, named("user_session"))
    // Session instances now available
}

// Logout
fun onLogout(userId: String) {
    getKoin().getScopeOrNull(userId)?.close()
    // Session instances released
}
```

### Feature Scope

```kotlin
val appModule = module {
    scope(named("checkout")) {
        scoped { CheckoutNavigator() }
        scoped { CheckoutPresenter(get()) }
    }
}

class CheckoutActivity : AppCompatActivity(), AndroidScopeComponent {
    override val scope: Scope by lazy {
        getKoin().createScope("checkout_${hashCode()}", named("checkout"))
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.close()
    }
}
```

## Best Practices

1. **Use singletons sparingly** - Only for truly app-wide dependencies
2. **Scope shared state** - When multiple components need the same instance
3. **Close scopes explicitly** - Use `scope.use { }` for short-lived scopes, or call `close()` manually
4. **Keep scopes focused** - Don't put everything in one scope
5. **Use Android scope components** - For automatic lifecycle management

## Next Steps

- **[Koin for Android](/docs/integrations/android/android-scopes)** - Android-specific scopes
- **[Koin for Compose](/docs/integrations/compose/compose-modules)** - Scopes in Compose
- **[Best Practices](/docs/best-practices/custom-scopes)** - Scope patterns
