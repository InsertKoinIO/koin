---
title: Scopes in Compose
description: Bind a Koin scope to a Composable, a navigation destination, an Activity or a Fragment, and resolve scoped dependencies with koinInject.
---

This page covers the Compose functions that open a Koin scope for a part of the UI tree: `KoinScope`, `KoinNavigationScope`, `UnboundKoinScope`, and the Android `KoinActivityScope` and `KoinFragmentScope`. Inside such a block, `koinInject()` and `koinViewModel()` resolve from that scope. How scopes work and how to declare them is explained in [Scopes](/docs/reference/koin-core/scopes).

Most functions on this page are marked `@KoinExperimentalAPI`. Opt in with `@OptIn(KoinExperimentalAPI::class)`.

## KoinScope

`KoinScope` gets or creates a scope, provides it to its children, and closes it when the Composable leaves the composition.

Declare the scope:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class FeatureScope

val featureModule = module {
    scope<FeatureScope> {
        scoped<FeatureCache>()
        scoped<FeatureRepository>()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
class FeatureScope

@Scope(FeatureScope::class)
class FeatureCache

@Scope(FeatureScope::class)
class FeatureRepository(private val cache: FeatureCache)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
class FeatureScope

val featureModule = module {
    scope<FeatureScope> {
        scopedOf(::FeatureCache)
        scopedOf(::FeatureRepository)
    }
}
```

</TabItem>
</Tabs>

Open it in Compose:

```kotlin
import org.koin.compose.scope.KoinScope

@Composable
fun FeatureScreen() {
    KoinScope<FeatureScope>(scopeID = "feature") {
        FeatureContent()
    }
}

@Composable
fun FeatureContent() {
    val repository = koinInject<FeatureRepository>() // resolved from the FeatureScope
}
```

`KoinScope` has three forms:

| Form | Scope |
|------|-------|
| `KoinScope<T>(scopeID) { }` | Gets or creates the scope with that id, qualified by type `T` |
| `KoinScope(scopeID, scopeQualifier) { }` | Gets or creates the scope with that id and qualifier |
| `KoinScope(scopeDefinition = { createScope(...) }) { }` | The scope returned by the lambda, which runs on the `Koin` instance |

The scope is closed when the Composable is forgotten (it leaves the composition) or abandoned. A root scope is never closed.

### Multi-screen flows

Put `KoinScope` above a `NavHost` to share scoped objects between the screens of a flow, such as a checkout:

```kotlin
@Composable
fun CheckoutFlow() {
    KoinScope<CheckoutFlowScope>(scopeID = "checkout") {
        NavHost(navController, startDestination = CartRoute) {
            composable<CartRoute> { CartScreen() }
            composable<PaymentRoute> { PaymentScreen() }
        }
    }
}

@Composable
fun PaymentScreen() {
    val state = koinInject<CheckoutState>() // same CheckoutState as in CartScreen
}
```

The scope closes when `CheckoutFlow` leaves the composition.

:::note
A `viewModel` declared in the scope is still stored in the current `ViewModelStoreOwner`. Inside a `NavHost`, each destination gets its own ViewModel instance. Share state between screens through a `scoped` object such as `CheckoutState`, or use `sharedKoinViewModel()` (see [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel)).
:::

## rememberKoinScope

`rememberKoinScope(scope)` binds a scope you created to the composition, without providing it to children. The scope is closed when the Composable leaves the composition:

```kotlin
@Composable
fun FeatureScreen() {
    val koin = getKoin()
    val scope = rememberKoinScope(remember { koin.createScope<FeatureScope>("feature") })
    val repository = koinInject<FeatureRepository>(scope = scope)
}
```

## KoinNavigationScope

`KoinNavigationScope` opens one scope per Navigation Compose destination. It is in `io.insert-koin:koin-compose-viewmodel-navigation`.

Declare the definitions with `navigationScope { }`. It creates a scope qualified by `NavBackStackEntry`:

```kotlin
import org.koin.module.dsl.navigationScope

val appModule = module {
    navigationScope {
        scoped<ScreenStateHolder>()
        viewModel<ScreenViewModel>()
    }
}
```

With annotations or the Constructor DSL, use the same `navigationScope { }` block in a DSL module (with `scopedOf(::ScreenStateHolder)` in the Constructor DSL). There is no annotation for this scope.

Wrap each destination:

```kotlin
NavHost(navController, startDestination = ListRoute) {
    composable<ListRoute> { entry ->
        KoinNavigationScope(entry) {
            ListScreen() // its own ScreenStateHolder
        }
    }
    composable<DetailRoute> { entry ->
        KoinNavigationScope(entry) {
            DetailScreen() // another ScreenStateHolder
        }
    }
}
```

The scope id is `NavBackStackEntry.id`. The scope is not closed when the destination leaves the composition, so it survives while the entry stays on the back stack. It is closed only if the composition is abandoned.

:::warning
`KoinNavigationScope` creates its scope in the global Koin instance (`KoinPlatform.getKoin()`), not in the Koin of the composition. It does not work inside `KoinIsolatedContext`.
:::

## UnboundKoinScope

`UnboundKoinScope` provides a scope you manage yourself. Koin never closes it:

```kotlin
@OptIn(KoinDelicateAPI::class, KoinExperimentalAPI::class)
@Composable
fun SessionArea(sessionScope: Scope) {
    UnboundKoinScope(scope = sessionScope) {
        SessionContent()
    }
}
```

Use it when the scope lifetime does not follow the UI tree: a user session opened at login, a scope shared by several composition roots. It is marked `@KoinDelicateAPI`: you must call `scope.close()` yourself, or the scope and its instances stay in memory.

## Android scopes

These functions are in `io.insert-koin:koin-androidx-compose` (`KoinActivityScope`, `KoinFragmentScope`) and in the Android target of `koin-compose` (`koinActivityInject`). The Activity or Fragment must implement `AndroidScopeComponent`. Declaring Activity and Fragment scopes is covered in [Android Scopes](/docs/reference/koin-android/scope).

```kotlin
class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoinActivityScope {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val presenter = koinInject<MainPresenter>() // resolved from the Activity scope
}
```

`KoinFragmentScope { }` does the same in a `ComposeView` hosted by a Fragment.

`koinActivityInject()` resolves from the Activity scope at any depth, without a wrapping block:

```kotlin
@Composable
fun DeepNestedScreen() {
    val session: SessionManager = koinActivityInject()
}
```

It fails if `LocalActivity` does not implement `AndroidScopeComponent`.

## Comparison

| API | Scope lifetime | Closed by |
|-----|----------------|-----------|
| `KoinScope` | The Composable | Koin, when the Composable leaves the composition |
| `rememberKoinScope` | The Composable | Koin, when the Composable leaves the composition |
| `KoinNavigationScope` | The back stack entry | Koin, only if the composition is abandoned |
| `UnboundKoinScope` | Yours | You |
| `KoinActivityScope` | The Activity | The Activity scope (`activityScope()`) |
| `KoinFragmentScope` | The Fragment | The Fragment scope (`fragmentScope()`) |

## Related

- [Scopes](/docs/reference/koin-core/scopes): declare scopes, link them, close them.
- [Android Scopes](/docs/reference/koin-android/scope): Activity, Fragment and ViewModel scopes.
- [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel): share a ViewModel across a navigation graph.
- [Lifecycle and State](/docs/reference/koin-compose/compose-lifecycle): what each Compose API releases and when.
- [Dynamic Modules](/docs/reference/koin-compose/compose-modules): load modules with a Composable.
