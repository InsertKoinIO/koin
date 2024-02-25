---
title: Managing Android Scopes
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

## Scope for Android Components (3.2.1 update)

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

### AndroidScopeComponent and Scope closing handling

You can run some code before Koin Scope is being destroyed, by overriding `onCloseScope` function from `AndroidScopeComponent`:

```kotlin
class MyActivity() : AppCompatActivity(contentLayoutId), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    override fun onCloseScope() {
        // Called before closing the Scope
    }
}
```

:::note
If you try to access Scope from `onDestroy()` function, scope will be already closed.
:::

## Scope Links

Scope links allow to share instances between components with custom scopes.

In a more extended usage, you can use a `Scope` instance across components. For example, if we need to share a `UserSession` instance.

First declare a scope definition:

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
