---
title: Android Scopes
description: Bind instances to an Activity, Fragment, Service or ViewModel lifecycle with activityScope, fragmentScope, activityRetainedScope and ScopeActivity.
---

An Android scope keeps instances alive as long as one component (an Activity, a Fragment, a Service or a ViewModel) and closes them with it. This page covers the Android scope APIs: scope archetypes, `ScopeActivity`, `ScopeFragment`, `AndroidScopeComponent`, retained and Service scopes. What a scope is, `scoped`, linking and closing in general are explained in [Scopes](/docs/reference/koin-core/scopes).

## Scope lifetimes

| Scope | Opened | Closed | Survives rotation |
|-------|--------|--------|-------------------|
| `activityScope` | First access in the Activity | Activity `ON_DESTROY` | No |
| `activityRetainedScope` | First access in the Activity | Activity finishes (backed by a ViewModel) | Yes |
| `fragmentScope` | First access in the Fragment | Fragment `ON_DESTROY` | No |
| Service scope | `Service.onCreate()` | `Service.onDestroy()` | Not applicable |
| `viewModelScope` | ViewModel creation | ViewModel cleared | Yes |

A `single` lives as long as the app, so it is the outer level for all of them.

## Declaring scoped definitions

Android has one scope archetype per component type. An archetype is a scope declaration that every Activity (or Fragment) with a scope can use, so you don't write one scope per class:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.androidx.scope.dsl.activityScope
import org.koin.androidx.scope.dsl.fragmentScope

val screenModule = module {
    activityScope {
        scoped<ScreenNavigator>()
    }
    activityRetainedScope {
        scoped<FormState>()
    }
    fragmentScope {
        scoped<DetailPresenter>()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.android.annotation.ActivityRetainedScope
import org.koin.android.annotation.ActivityScope
import org.koin.android.annotation.FragmentScope

@ActivityScope
class ScreenNavigator(private val activity: Activity)

@ActivityRetainedScope
class FormState

@FragmentScope
class DetailPresenter(private val repository: UserRepository)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.androidx.scope.dsl.activityScope
import org.koin.androidx.scope.dsl.fragmentScope

val screenModule = module {
    activityScope {
        scopedOf(::ScreenNavigator)
    }
    activityRetainedScope {
        scopedOf(::FormState)
    }
    fragmentScope {
        scopedOf(::DetailPresenter)
    }
}
```

</TabItem>
</Tabs>

To restrict definitions to one Activity class, declare a type-based scope instead (`scope<UserActivity> { }` in the DSL, `@Scope(UserActivity::class)` with `@Scoped` in annotations). Koin looks in the type-based scope first, then in the archetype. Type-based scopes are explained in [Scopes](/docs/reference/koin-core/scopes).

## Activity scope

Extend `ScopeActivity`, or implement `AndroidScopeComponent` and create the scope with `activityScope()`:

```kotlin
import org.koin.androidx.scope.ScopeActivity

class UserActivity : ScopeActivity() {

    private val navigator: ScreenNavigator by inject()
}
```

```kotlin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope

class UserActivity : AppCompatActivity(), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    private val navigator: ScreenNavigator by inject()
}
```

With a scope, `inject()`, `get()` and `viewModel()` in the Activity resolve from the Activity scope first, then from the root scope. The scope closes on the Activity `ON_DESTROY` lifecycle event, so it is closed by the time your `onDestroy()` code runs.

The Activity is the source of its scope. A scoped definition can ask for `Activity` or `Context` and receive the current Activity:

```kotlin
@ActivityScope
class ScreenNavigator(private val activity: Activity) {
    fun openSettings() = activity.startActivity(Intent(activity, SettingsActivity::class.java))
}
```

`activityScope()` (the delegate, in `org.koin.androidx.scope`) and `activityScope { }` (the module DSL, in `org.koin.androidx.scope.dsl`) share a name. Check the import when the compiler picks the wrong one.

### Retained Activity scope

`activityRetainedScope()` keeps the same scope across configuration changes. Koin stores it in a ViewModel of the Activity and closes it when that ViewModel is cleared, so when the Activity finishes:

```kotlin
import org.koin.androidx.scope.RetainedScopeActivity

class FormActivity : RetainedScopeActivity() {

    private val formState: FormState by inject()
}
```

Or, with `AndroidScopeComponent`: `override val scope: Scope by activityRetainedScope()`.

Definitions in a retained scope outlive the Activity instance. They must not keep a reference to the Activity, its Views or a Fragment. A retained scope has no source: `Context` resolves to the `Application` there.

## Fragment scope

Extend `ScopeFragment`, or implement `AndroidScopeComponent` with `fragmentScope()`:

```kotlin
import org.koin.androidx.scope.ScopeFragment

class DetailFragment : ScopeFragment() {

    private val presenter: DetailPresenter by inject()

    // resolved from the parent Activity scope
    private val navigator: ScreenNavigator by inject()
}
```

```kotlin
import org.koin.androidx.scope.fragmentScope

class DetailFragment : Fragment(), AndroidScopeComponent {

    override val scope: Scope by fragmentScope()
}
```

The Fragment scope is linked to the host Activity scope, so it sees the Activity scoped instances. This works only if the host Activity has a scope (`ScopeActivity` or `AndroidScopeComponent`); otherwise the link is skipped and Koin logs it. Pass `fragmentScope(useParentActivityScope = false)` to skip it on purpose.

:::warning
The Fragment scope follows the Fragment lifecycle (`ON_DESTROY`), not the view lifecycle. A scoped instance that holds a View or a binding keeps it after `onDestroyView()`, until the Fragment is destroyed. Release View references in `onDestroyView()`.
:::

## Service scope

Extend `ScopeService`. Its scope opens in `onCreate()` and closes in `onDestroy()`. There is no Service archetype: declare a type-based scope for the Service class.

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val playerModule = module {
    scope<MusicService> {
        scoped<PlaybackSession>()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Scope(MusicService::class)
@Scoped
class PlaybackSession
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val playerModule = module {
    scope<MusicService> {
        scopedOf(::PlaybackSession)
    }
}
```

</TabItem>
</Tabs>

```kotlin
import org.koin.android.scope.ScopeService

class MusicService : ScopeService() {

    private val session: PlaybackSession by inject()

    override fun onBind(intent: Intent?): IBinder? = null
}
```

If your Service can't extend `ScopeService`, implement `AndroidScopeComponent` with `override val scope: Scope by serviceScope()` and call `destroyServiceScope()` in `onDestroy()`.

## ViewModel scope

A ViewModel scope holds instances that live exactly as long as one ViewModel. Declare them with `viewModelScope { }` or `@ViewModelScope`. Then either enable the `viewModelScopeFactory()` option, so Koin creates a scope for each ViewModel, or extend `ScopeViewModel`:

```kotlin
import org.koin.core.option.viewModelScopeFactory

startKoin {
    androidContext(this@MainApplication)
    options(viewModelScopeFactory())
    modules(appModule)
}
```

Declaration, the option and `ScopeViewModel` are explained in [Scopes](/docs/reference/koin-core/scopes). On Android, retrieve the ViewModel with `by viewModel()` as usual (see [Android ViewModel](/docs/reference/koin-android/viewmodel)).

Don't declare a ViewModel with `scoped` in an `activityScope`: you get a plain object, not managed by the `ViewModelStore`, recreated on every rotation. Declare it with `viewModel` and retrieve it with `by viewModel()`.

## Cleaning up before the scope closes

Override `onCloseScope()` in an `AndroidScopeComponent`. Koin calls it right before it closes an Activity or Fragment scope, while scoped instances are still available:

```kotlin
class UserActivity : ScopeActivity() {

    override fun onCloseScope() {
        get<ScreenNavigator>().clear()
    }
}
```

`onCloseScope()` is not called for a retained scope or a Service scope. For cleanup tied to one definition, use `onClose { }` on the definition (see [Definitions](/docs/reference/koin-core/definitions#release-callback)).

## Limits

- Scope required: a class that injects scoped definitions must have a scope (`ScopeActivity`, `ScopeFragment`, `ScopeService` or `AndroidScopeComponent`). Without one, `inject()` resolves from the root scope and fails with `NoDefinitionFoundException`.
- Main thread: create the Activity and Fragment scopes from the main thread (property delegates read in or after `onCreate()`).
- Custom scopes: a scope you create yourself with `getKoin().createScope()` is not tied to any lifecycle. Close it yourself.

:::tip
To see scope lifetimes and leaked instances in real user sessions, see [Production Monitoring](/docs/tooling/production-monitoring).
:::

## Related

- [Scopes](/docs/reference/koin-core/scopes): scope concepts, linking, `@Scope` and the ViewModel scope.
- [Android ViewModel](/docs/reference/koin-android/viewmodel): ViewModels with Activity and Fragment scopes.
- [Android Entry Points](/docs/reference/koin-android/entry-points): inject in Android components.
- [Fragment Factory](/docs/reference/koin-android/fragment-factory): create Fragments from an Activity scope.
- [Android Best Practices](/docs/reference/koin-android/best-practices): avoid Context and Activity leaks.
