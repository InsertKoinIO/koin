
The `koin-android-scope` project is dedicated to bring Android scope features to the existing Scope API.


## Taming the Android lifecycle

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

    scope(named("scope_id")) {
        scoped { Presenter() }
    }
}
```

!> Most of Android memory leaks comes from referencing a UI/Android component from a non Android component. The system keeps a reference
on it and can't totally drop it via garbage collection.

## LifecycleScope - a scope tied to your lifecycle

Koin gives the `lifecycleScope` property already bound to your Android component lifecycle. On lifecycle's end, it will close automatically.

To benefit from the `lifecycleScope`, you have to declare a scope for your activity (tied our Activity type):

```kotlin
val androidModule = module {

    scope<MyActivity> {
        scoped { Presenter() }
    }
}
```

```kotlin
class MyActivity : AppCompatActivity() {

    // inject Presenter instance from current scope
    val presenter : Presenter by lifecycleScope.inject()

```


!> Be careful to not use `scope` but `lifecycleScope`. This is a scope tied to the Android lifecycle. Else your scoped instances won't follow your lifecycle.

> If you have any conflict with any API that has also a `lifecycleScope` extension, please name your extension - https://github.com/InsertKoinIO/koin/issues/679

## Sharing instances between components with scopes

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
```

Then use it anywhere you need it:

```kotlin
class MyActivity1 : AppCompatActivity() {

    val userSession : UserSession by ourSession.inject()
}
class MyActivity2 : AppCompatActivity() {

    val userSession : UserSession by ourSession.inject()
}
```

or you can also inject it with Koin DSL. If a presenter need it:

```kotlin
class Presenter(val userSession : UserSession)
```

Just inject it into constructor, with the right scope id:

```kotlin
module {
    // Shared user session data
    scope(named("session")) {
        scoped { UserSession() }
    }

    // Inject UserSession instance from "session" Scope
    factory { (scopeId : ScopeID) -> Presenter(getScope(scopeId).get())}
}
```

When you have to finish with your scope, just close it:

```kotlin
val ourSession = getKoin().getScope("ourSession")
ourSession.close()
```

