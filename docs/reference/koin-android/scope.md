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
If you try to access Scope from `onDestroy()` function, scope will be already closed.
:::

### ViewModel Scope (since 3.5.4)

ViewModel is only created against root scope to avoid any leaking (leaking Activity or Fragment ...). This guard for the visibility problem, where ViewModel could have access to incompatible scopes.

:::warn
ViewModel can't access to Activity or Fragment scope. Why? Because ViewModel is lasting long than Activity and Fragment, and then it would leak dependencies outside of proper scopes.
:::

:::note
If you _really_ need to bridge a dependency from outside a ViewModel scope, you can use "injected parameters" to pass some objects to your ViewModel: `viewModel { p ->  }`
:::

`ScopeViewModel` is a new class to help work on ViewModel scope. This handle ViewModel's scope creation, and provide `scope` property to allow inject with `by scope.inject()`:

```kotlin
module {
    viewModelOf(::MyScopeViewModel)
    scope<MyScopeViewModel> {
        scopedOf(::Session)
    }
    // or
    viewModelScope {
        scopedOf(::Session)
    }
}

class MyScopeViewModel : ScopeViewModel() {

    // on onCleared, scope is closed
    
    // injected from current MyScopeViewModel's scope
    val session by scope.inject<Session>()

}
```

By using `ScopeViewModel` you can also overrode `onCloseScope()` function, to run code before scope is being closed.

:::note
All instances inside a ViewModel scope have the same visibility and will survive for lifetime of ViewModel instance, until ViewModel's onCleared function is called
:::

For example, Once an Activity or fragment has created a ViewModel, the associated scope is created:

```kotlin
class MyActivity : AppCompatActivity() {

    // Create ViewModel and its scope
    val myViewModel by viewModel<MyScopeViewModel>()

}
```

Once your ViewModel is created, all associated dependencies from within this scope can be created and injected.

To implement manually your ViewModel scope without `ScopeViewModel` class proceed as follow:

```kotlin
class MyScopeViewModel : ViewModel(), KoinScopeComponent {

    override val scope: Scope = createScope(this)

    // inject your dependency
    val session by scope.inject<Session>()

    // clear scope
    override fun onCleared() {
        super.onCleared()
        scope.close()
    }
}
```
### Scope Archetypes (4.1.0) [Experimental]

As new feature, you can now declare scope by archetype: you don't require to define a scope against a type but against an "archetype". You can declare a scope for "Activity", "Fragment" and "ViewModel":

```kotlin

module {
 activityScope {
  // scoped instances for an An activity
 }

 activityRetainedScope {
  // scoped instances for an An activity, retained scope
 }

 fragmentScope {
  // scoped instances for Fragment
 }

 viewModelScope {
  // scoped instances for ViewModel
 }
}
```

This will help reuse instances in between scopes easily. No need to use a specific type like `scope<>{ }`, apart if you need scope on a precise object.

Use the regular `activityScope()`, `activityRetainedScope()` and `fragmentScope()` delegates function to create API. Those will trigger scope archetypes. Also you can use Koin's `ScopeActivity` or `ScopeFragment` classes.

For ViewModel's scope, you can use `ScopeViewModel` class, or deal yourself with the API and `viewModelScope()` function:

```kotlin
class MyViewModel : ViewModel(), KoinScopeComponent {

    override val scope: Scope = viewModelScope()

    override fun onCleared() {
        // Close Koin scope here
        scope.close()
     
        super.onCleared()
    }
}
```

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
