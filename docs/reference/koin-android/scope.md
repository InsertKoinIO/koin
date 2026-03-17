---
title: Android Scopes
---

This guide covers Android-specific scope implementations. 

:::info
For core scope concepts, see [Scopes](/docs/reference/koin-core/scopes).
:::

## Overview

Scopes in Koin allow you to manage the lifecycle of your dependencies to match Android component lifecycles. This prevents memory leaks and ensures proper resource management.

### Scope Hierarchy

| Scope Type | Lifetime | Survives Rotation | DSL | Annotation |
|------------|----------|-------------------|-----|------------|
| **Application** | Entire app | ✅ Yes | `single { }` | `@Singleton` |
| **Activity** | Activity lifecycle | ❌ No | `activityScope { }` | `@ActivityScope` |
| **Activity Retained** | Until finish() | ✅ Yes | `activityRetainedScope { }` | `@ActivityRetainedScope` |
| **Fragment** | Fragment lifecycle | ❌ No | `fragmentScope { }` | `@FragmentScope` |
| **ViewModel** | ViewModel lifecycle | ✅ Yes | `viewModelScope { }` | `@ViewModelScope` |

### Scope Relationships

```
Application Scope (single { })
    └── Activity Retained Scope (survives rotation)
            └── Activity Scope
                    ├── Fragment Scope 1
                    └── Fragment Scope 2
            └── ViewModel Scope (can't access Activity/Fragment scope)
```

:::info
**Key Principle:** Child scopes can access parent scope definitions, but not vice versa.
:::

## Declaring Scoped Dependencies

### Compiler Plugin DSL

```kotlin
val appModule = module {
    // Activity scope
    activityScope {
        scoped<ActivityPresenter>()
        scoped<ActivityNavigator>()
    }

    // Fragment scope
    fragmentScope {
        scoped<FragmentPresenter>()
    }

    // ViewModel scope
    viewModelScope {
        scoped<UserCache>()
        viewModel<UserViewModel>()
    }
}
```

### Annotations

```kotlin
// Activity scope
@ActivityScope
class ActivityPresenter(private val repository: UserRepository)

@ActivityScope
class ActivityNavigator

// Activity retained scope (survives rotation)
@ActivityRetainedScope
class RetainedPresenter

// Fragment scope
@FragmentScope
class FragmentPresenter

// ViewModel scope
@ViewModelScope
class UserCache

@KoinViewModel
@ViewModelScope
class UserViewModel(private val cache: UserCache) : ViewModel()
```

### Classic DSL

```kotlin
val appModule = module {
    activityScope {
        scoped { ActivityPresenter(get()) }
        scoped { ActivityNavigator() }
    }

    fragmentScope {
        scoped { FragmentPresenter(get()) }
    }

    viewModelScope {
        scoped { UserCache() }
        viewModel { UserViewModel(get()) }
    }
}
```

## Using Scopes in Android Components

### Activity Scope

```kotlin
class MyActivity : AppCompatActivity(), AndroidScopeComponent {

    // Create scope tied to Activity lifecycle
    override val scope: Scope by activityScope()

    // Inject from scope
    private val presenter: ActivityPresenter by inject()
}
```

Or use the convenience base class:

```kotlin
class MyActivity : ScopeActivity() {

    // Scope is already set up
    private val presenter: ActivityPresenter by inject()
}
```

### Activity Retained Scope

Survives configuration changes (rotation, theme change):

```kotlin
class MyActivity : AppCompatActivity(), AndroidScopeComponent {

    // Backed by ViewModel lifecycle - survives rotation
    override val scope: Scope by activityRetainedScope()

    private val presenter: RetainedPresenter by inject()
}
```

Or use the convenience base class:

```kotlin
class MyActivity : RetainedScopeActivity() {

    private val presenter: RetainedPresenter by inject()
}
```

### Fragment Scope

Fragment scopes are automatically linked to parent Activity scope:

```kotlin
class MyFragment : Fragment(), AndroidScopeComponent {

    override val scope: Scope by fragmentScope()

    // From fragment scope
    private val presenter: FragmentPresenter by inject()

    // Can also access Activity scope dependencies
    private val activityPresenter: ActivityPresenter by inject()
}
```

Or use the convenience base class:

```kotlin
class MyFragment : ScopeFragment() {

    private val presenter: FragmentPresenter by inject()
}
```

## Type-Based vs Archetype Scopes

### Archetype Scope (Recommended)

Generic scope that works with any Activity/Fragment:

```kotlin
module {
    activityScope {
        scoped<MyPresenter>()
    }
}

// Works with any Activity
class ActivityA : ScopeActivity() {
    private val presenter: MyPresenter by inject()
}

class ActivityB : ScopeActivity() {
    private val presenter: MyPresenter by inject()
}
```

### Type-Based Scope

Scope tied to a specific class:

```kotlin
module {
    scope<MyActivity> {
        scoped<MyPresenter>()
    }
}

// Only works with MyActivity
class MyActivity : AppCompatActivity(), AndroidScopeComponent {
    override val scope: Scope by activityScope()
    private val presenter: MyPresenter by inject()
}
```

## ViewModel Scope

ViewModels cannot access Activity or Fragment scopes (to prevent memory leaks). Use ViewModel Scope for scoped dependencies:

```kotlin
module {
    viewModelScope {
        scoped<UserCache>()
        scoped<UserRepository>()
        viewModel<UserViewModel>()
    }
}
```

```kotlin
@ViewModelScope
class UserCache

@ViewModelScope
class UserRepository(private val cache: UserCache)

@KoinViewModel
@ViewModelScope
class UserViewModel(
    private val repository: UserRepository
) : ViewModel()
```

For detailed ViewModel scope usage, see [Scopes - ViewModel Scope](/docs/reference/koin-core/scopes#viewmodel-scope).

## Scope Lifecycle

### Handling Scope Close

Override `onCloseScope()` to run cleanup before scope is destroyed:

```kotlin
class MyActivity : AppCompatActivity(), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    override fun onCloseScope() {
        // Called BEFORE scope.close()
        // Scope is still accessible here
    }
}
```

:::warning
Don't access scope in `onDestroy()` - it's already closed at that point.
:::

## Scope Links

Share instances between components with custom scopes:

```kotlin
module {
    scope(named("session")) {
        scoped<UserSession>()
    }
}
```

```kotlin
class MyActivity : ScopeActivity() {

    fun startSession() {
        val sessionScope = getKoin().createScope("session", named("session"))

        // Link to current scope
        scope.linkTo(sessionScope)

        // Now UserSession is accessible
        val session: UserSession = get()
    }
}
```

## Quick Reference

| Component | Delegate | Base Class |
|-----------|----------|------------|
| Activity | `by activityScope()` | `ScopeActivity` |
| Activity (retained) | `by activityRetainedScope()` | `RetainedScopeActivity` |
| Fragment | `by fragmentScope()` | `ScopeFragment` |

| Scope | Survives Rotation | Use Case |
|-------|-------------------|----------|
| `activityScope` | ❌ No | UI state, presenters |
| `activityRetainedScope` | ✅ Yes | Form state, pending requests |
| `fragmentScope` | ❌ No | Fragment-specific presenters |
| `viewModelScope` | ✅ Yes | ViewModel dependencies |

## Best Practices

1. **Use archetypes** - Prefer `activityScope { }` over `scope<MyActivity> { }` for reusability
2. **Retained for rotation** - Use `activityRetainedScope` for state that should survive rotation
3. **Don't leak** - Never inject Activity/Fragment into singletons
4. **Close custom scopes** - Always close manually created scopes
5. **Use onCloseScope** - For cleanup before scope destruction

## Next Steps

- **[Core Scopes](/docs/reference/koin-core/scopes)** - Scope fundamentals and ViewModel scope
- **[ViewModel](/docs/reference/koin-android/viewmodel)** - ViewModel injection
- **[Testing](/docs/reference/koin-test/testing)** - Testing scoped dependencies
