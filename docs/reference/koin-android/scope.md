---
title: Android Scopes
---


## Working with the Android lifecycle

Android components are mainly managed by their lifecycle: we can't directly instantiate an Activity nor a Fragment. The system
make all creation and management for us, and make callbacks on methods: onCreate, onStart...

That's why we can't describe our Activity/Fragment/Service in a Koin module. We need then to inject dependencies into properties and also
respect the lifecycle: Components related to the UI parts must be released on soon as we don't need them anymore.

Then we have:

* long live components (Services, Data Repository ...) - used by several screens, never dropped
* medium live components (user sessions ...) - used by several screens, must be dropped after an amount of time
* short live components (views) - used by only one screen & must be dropped at the end of the screen

Long live components can be easily described as `single` definitions. For medium and short live components we can have several approaches.

In the case of MVP architecture style, the `Presenter` is a short live component to help/support the UI. The presenter must be created each time the screen is showing,
and dropped once the screen is gone.

A new Presenter is created each time

```kotlin
class DetailActivity : AppCompatActivity() {

    // injected Presenter
    override val presenter : Presenter by inject()
```

We can describe it in a module:


* as `factory` - to produce a new instance each time the `by inject()` or `get()` is called

```kotlin
val androidModule = module {

    // Factory instance of Presenter
    factory { Presenter() }
}
```

* as `scope` - to produce an instance tied to a scope

```kotlin
val androidModule = module {

    scope<DetailActivity> {
        scoped { Presenter() }
    }
}
```

:::note
Most of Android memory leaks come from referencing a UI/Android component from a non Android component. The system keeps a reference
on it and can't totally drop it via garbage collection.
:::

## Scope for Android Components (since 3.2.1)

### Declare an Android Scope

To scope dependencies on an Android component, you have to declare a scope section with the `scope` block like follow:

```kotlin
class MyPresenter()
class MyAdapter(val presenter : MyPresenter)

module {
  // Declare scope for MyActivity
  scope<MyActivity> {
   // get MyPresenter instance from current scope 
   scoped { MyAdapter(get()) }
   scoped { MyPresenter() }
  }
 
  // or
  activityScope {
   scoped { MyAdapter(get()) }
   scoped { MyPresenter() }
  }
}
```

### Android Scope Classes

Koin offers `ScopeActivity`, `RetainedScopeActivity` and `ScopeFragment` classes to let you use directly a declared scope for Activity or Fragment:

```kotlin
class MyActivity : ScopeActivity() {
    
    // MyPresenter is resolved from MyActivity's scope 
    val presenter : MyPresenter by inject()
}
```

Under the hood, Android scopes needs to be used with `AndroidScopeComponent` interface to implement `scope` field like this:

```kotlin
abstract class ScopeActivity(
    @LayoutRes contentLayoutId: Int = 0,
) : AppCompatActivity(contentLayoutId), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotNull(scope)
    }
}
```

We need to use the `AndroidScopeComponent` interface and implement the `scope` property. This will setting up the default scope used by your class.

### Android Scope API

To create a Koin scope bound to an Android component, just use the following functions:
- `createActivityScope()` - Create Scope for current Activity (scope section must be declared)
- `createActivityRetainedScope()` - Create a retained Scope (backed by ViewModel lifecycle) for current Activity (scope section must be declared)
- `createFragmentScope()` - Create Scope for current Fragment and link to parent Activity scope

Those functions are available as delegate, to implement different kind of scope:

- `activityScope()` - Create Scope for current Activity (scope section must be declared)
- `activityRetainedScope()` - Create a retained Scope (backed by ViewModel lifecycle) for current Activity (scope section must be declared)
- `fragmentScope()` - Create Scope for current Fragment and link to parent Activity scope

```kotlin
class MyActivity() : AppCompatActivity(contentLayoutId), AndroidScopeComponent {

    override val scope: Scope by activityScope()
    
}
```

We can also to setting up a retained scope (backed by a ViewModel lifecycle) with the following:

```kotlin
class MyActivity() : AppCompatActivity(contentLayoutId), AndroidScopeComponent {

    override val scope: Scope by activityRetainedScope()
}
```

:::note
If you don't want to use Android Scope classes, you can work with your own and use `AndroidScopeComponent` with the Scope creation API
:::

### AndroidScopeComponent and handling Scope closing

You can run code before Koin Scope is destroyed, by overriding the `onCloseScope` function from `AndroidScopeComponent`:

```kotlin
class MyActivity() : AppCompatActivity(contentLayoutId), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    override fun onCloseScope() {
        // Called before closing the Scope
    }
}
```

:::note
If you try to access Scope from `onDestroy()` function, the scope will already be closed.
:::

### Scope Archetypes (4.1.0)

As a new feature, you can now declare scope by **archetype**: you don't need to define a scope against a specific type, but for an "archetype" (a kind of scope class). You can declare a scope for "Activity", "Fragment", or "ViewModel".
You can now use the following DSL sections:

```kotlin
module {
 activityScope {
  // scoped instances for an activity
 }

 activityRetainedScope {
  // scoped instances for an activity, retained scope
 }

 fragmentScope {
  // scoped instances for Fragment
 }

 viewModelScope {
  // scoped instances for ViewModel
 }
}
```

This allows for better reuse of definitions between scopes easily. No need to use a specific type like `scope<>{ }`, apart from if you need scope on a precise object.

:::info
See [Android Scope API](#android-scope-api) to see how to use `by activityScope()`, `by activityRetainedScope()`, and `by fragmentScope()` functions to activate your Android scope. Those functions will trigger scope archetypes.
:::

For example, you can easily scope a defintion to an activity like that, with Scope Archetypes:

```kotlin
// declare Class Session in Activity scope
module {
 activityScope {
    scopedOf(::Session)
 }
}

// Inject the scoped Session object to the activity:
class MyActivity : AppCompatActivity(), AndroidScopeComponent {
    
    // create Activity's scope
    val scope: Scope by activityScope() 
    
    // inject from scope above
    val session: Session by inject()
}
```

### ViewModel Scope (updated in 4.2.0)

ViewModel is only created against the root scope to avoid any leaking (leaking Activity or Fragment ...). This guards for the visibility problem, where the ViewModel could have access to incompatible scopes.

:::warning
ViewModel can't access Activity or Fragment scope. Why? Because ViewModel outlives Activity and Fragment, and would leak dependencies outside of proper scopes.
If you need to pass data from Activity/Fragment to ViewModel, use "injected parameters": `viewModel { params -> }`
:::

#### When to Use ViewModel Scope

Use ViewModel Scope when your ViewModel needs **scoped dependencies that are tied to its lifecycle**. Common use cases:
- Session data specific to a ViewModel
- Caches that should be cleared when ViewModel is destroyed
- Coordinators or use cases that should live as long as the ViewModel

#### Declaring ViewModel Scope

You can declare a ViewModel scope tied to a specific ViewModel class or using the `viewModelScope` archetype:

```kotlin
module {
    // Option 1: Scope for a specific ViewModel class
    scope<MyScopeViewModel> {
        scopedOf(::Session)
    }

    // Option 2: ViewModel Archetype scope - shared by all ViewModels
    viewModelScope {
        scopedOf(::Session)
    }
}
```

#### Manual Scope API

Use `KoinScopeComponent` and `viewModelScope()` function to manually manage the scope. Dependencies must be injected by field:

```kotlin
module {
    viewModelOf(::MyScopeViewModel)
    scope<MyScopeViewModel> {
        scopedOf(::Session)
    }
}

class MyScopeViewModel : ViewModel(), KoinScopeComponent {

    // Create ViewModel scope - handles creation and destruction
    override val scope: Scope = viewModelScope()

    // Inject from scope above
    val session: Session by inject()
}
```

#### Automatic Scope with Constructor Injection (Experimental)

For constructor injection of scoped dependencies, use the `viewModelScopeFactory` option.

:::caution Experimental Feature
This feature is experimental and requires opt-in via `@OptIn(KoinViewModelScopeApi::class)`.
**Important:** Your ViewModel must be declared **inside** the `viewModelScope { }` block for constructor injection to work.
:::

**Step 1: Enable the option**

```kotlin
@OptIn(KoinViewModelScopeApi::class)
startKoin {
    options(
        viewModelScopeFactory()
    )
}
```

**Step 2: Declare ViewModel inside viewModelScope block**

```kotlin
@OptIn(KoinViewModelScopeApi::class)
module {
    // ✅ Correct: ViewModel declared INSIDE viewModelScope
    viewModelScope {
        viewModel { MyScopeViewModel(get()) }  // or viewModelOf(::MyScopeViewModel)
        scoped { Session() }
    }
}

// ViewModel with constructor injection
class MyScopeViewModel(val session: Session) : ViewModel()
```

:::danger Common Mistake
Declaring the ViewModel **outside** the `viewModelScope` block will not work for constructor injection:

```kotlin
// ❌ Wrong: ViewModel outside viewModelScope - Session won't be found!
module {
    viewModelOf(::MyScopeViewModel)  // Declared at root scope
    viewModelScope {
        scoped { Session() }  // Session is in ViewModel scope
    }
}
```

The ViewModel is resolved from root scope and cannot see the scoped `Session` inside `viewModelScope`.
:::

**Step 3: Inject ViewModel**

```kotlin
class MyActivity : AppCompatActivity() {

    // Creates MyScopeViewModel and its scope
    val viewModel: MyScopeViewModel by viewModel()
}
```

#### How It Works

When `viewModelScopeFactory()` is enabled and a ViewModel is requested:
1. Koin creates a dedicated scope for the ViewModel
2. Dependencies declared in `viewModelScope { }` are resolved from this scope
3. The scope is automatically closed when the ViewModel is cleared

```
Root Scope
└── ViewModel Scope (created per ViewModel instance)
        └── scoped { Session() }  ← Available for constructor injection
```

#### Summary: Which Approach to Use?

| Approach | Constructor Injection | Field Injection | Opt-in Required |
|----------|----------------------|-----------------|-----------------|
| Manual (`KoinScopeComponent`) | ❌ No | ✅ Yes | No |
| Automatic (`viewModelScopeFactory`) | ✅ Yes | ✅ Yes | Yes (`@KoinViewModelScopeApi`) |

## Scope Links

Scope links allow sharing instances between components with custom scopes. By default, Fragment's scope are linked to parent Activity scope.

In a more extended usage, you can use a `Scope` instance across components. For example, if we need to share a `UserSession` instance.

First, declare a scope definition:

```kotlin
module {
    // Shared user session data
    scope(named("session")) {
        scoped { UserSession() }
    }
}
```

When needed to begin use a `UserSession` instance, create a scope for it:

```kotlin
val ourSession = getKoin().createScope("ourSession",named("session"))

// link ourSession scope to current `scope`, from ScopeActivity or ScopeFragment
scope.linkTo(ourSession)
```

Then use it anywhere you need it:

```kotlin
class MyActivity1 : ScopeActivity() {
    
    fun reuseSession(){
        val ourSession = getKoin().createScope("ourSession",named("session"))
        
        // link ourSession scope to current `scope`, from ScopeActivity or ScopeFragment
        scope.linkTo(ourSession)

        // will look at MyActivity1's Scope + ourSession scope to resolve
        val userSession = get<UserSession>()
    }
}
class MyActivity2 : ScopeActivity() {

    fun reuseSession(){
        val ourSession = getKoin().createScope("ourSession",named("session"))
        
        // link ourSession scope to current `scope`, from ScopeActivity or ScopeFragment
        scope.linkTo(ourSession)

        // will look at MyActivity2's Scope + ourSession scope to resolve
        val userSession = get<UserSession>()
    }
}
```
