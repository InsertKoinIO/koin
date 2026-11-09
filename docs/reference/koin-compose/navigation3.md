---
title: Navigation 3
description: Declare Navigation 3 entries in Koin modules with navigation<T>, and give NavDisplay an entry provider built from them.
---

This page shows how to declare [Navigation 3](https://developer.android.com/guide/navigation/navigation-3) entries in Koin modules and pass them to `NavDisplay`. Each feature module contributes its own screens, and the app collects them all with one entry provider. It assumes you know the Navigation 3 basics (back stack, `NavDisplay`, `NavEntry`) and [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel).

All APIs on this page are marked `@KoinExperimentalAPI`.

## Setup

```kotlin
// shared/build.gradle.kts, or app/build.gradle.kts on Android
plugins {
    kotlin("plugin.serialization")
}

commonMain.dependencies {
    implementation("io.insert-koin:koin-compose-navigation3:$koin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization_version")
}
```

`koin-compose-navigation3` targets Android, iOS, JVM desktop, JS and Wasm.

## Declaring entries

Routes are `@Serializable` classes:

```kotlin
@Serializable data object HomeRoute
@Serializable data class DetailRoute(val itemId: String)
```

Declare one `navigation<T> { }` per route in your modules. The lambda is a Composable that receives the route:

```kotlin
import org.koin.dsl.navigation3.navigation

val homeModule = module {
    viewModel<HomeViewModel>()

    navigation<HomeRoute> {
        HomeScreen(viewModel = koinViewModel())
    }
}

val detailModule = module {
    viewModel<DetailViewModel>()

    navigation<DetailRoute> { route ->
        DetailScreen(viewModel = koinViewModel { parametersOf(route.itemId) })
    }
}
```

Each `navigation<T>` is stored as a definition qualified by the route type. The lambda runs with the Koin `Scope` as receiver, so you can call `get<T>()` in it, as well as Composable functions such as `koinViewModel()`.

## Building the entry provider

`koinEntryProvider()` collects every `navigation<T>` entry of the current scope into one provider for `NavDisplay`:

```kotlin
import org.koin.compose.navigation3.koinEntryProvider

@Composable
fun App() {
    val backStack = remember { mutableStateListOf<Any>(HomeRoute) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = koinEntryProvider()
    )
}
```

`koinEntryProvider<T>()` takes your back stack's route type: `NavKey` with `rememberNavBackStack()`, or your own route type when `NavDisplay` is typed (a typed `SceneStrategy`, like `rememberSupportingPaneSceneStrategy<Route>()`, makes it `NavDisplay<Route>`). Left as `Any`, the build fails with: `Argument type mismatch: actual type is 'Function1<Any, NavEntry<Any>>', but 'Function1<Route, NavEntry<Route>>' was expected.`

### From an Android Activity

On Android, two extensions on `ComponentCallbacks` build the provider from the Activity or Fragment scope:

| Function | Behavior |
|----------|----------|
| `entryProvider<T>()` | Lazy delegate: `val provider by entryProvider<Any>()` |
| `getEntryProvider<T>()` | Builds the provider now |

They resolve from the component's Koin scope: the Activity scope if it implements `AndroidScopeComponent`, the root scope otherwise.

## Entries in a scope

Declare entries inside a scope when the screens need objects that live with that scope. With `activityRetainedScope`, they survive configuration changes:

```kotlin
class Navigator(start: Any) {
    val backStack = mutableStateListOf(start)
    fun goTo(route: Any) { backStack.add(route) }
    fun goBack() { backStack.removeLastOrNull() }
}

val appModule = module {
    activityRetainedScope {
        scoped { Navigator(start = ConversationList) }

        navigation<ConversationList> {
            val navigator = get<Navigator>()
            ConversationListScreen(onClick = { navigator.goTo(ConversationDetail(it)) })
        }

        navigation<ConversationDetail> { route ->
            ConversationDetailScreen(conversationId = route.id)
        }
    }
}

class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navigator: Navigator = get()
            NavDisplay(
                backStack = navigator.backStack,
                onBack = { navigator.goBack() },
                entryProvider = getEntryProvider()
            )
        }
    }
}
```

`getEntryProvider()` reads the entries from the Activity scope. Entries declared in a scope are visible only from that scope. `activityRetainedScope` is described in [Android Scopes](/docs/reference/koin-android/scope).

## ViewModels for an entry

To scope a ViewModel to its entry, add the ViewModel store decorator to `NavDisplay`. Without it, `koinViewModel()` uses the store of the Activity, and every entry of the same route shares one instance:

```kotlin
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator()
    ),
    entryProvider = koinEntryProvider()
)
```

Pass the route, or a field of it, as a parameter:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class DetailViewModel(
    @InjectedParam val route: DetailRoute,
    private val repository: ItemRepository
) : ViewModel()

val detailModule = module {
    viewModel<DetailViewModel>()

    navigation<DetailRoute> { route ->
        DetailScreen(viewModel = koinViewModel { parametersOf(route) })
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@KoinViewModel
class DetailViewModel(
    @InjectedParam val route: DetailRoute,
    private val repository: ItemRepository
) : ViewModel()

// navigation<T> entries are declared in a DSL module
val detailModule = module {
    navigation<DetailRoute> { route ->
        DetailScreen(viewModel = koinViewModel { parametersOf(route) })
    }
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
class DetailViewModel(
    val route: DetailRoute,
    private val repository: ItemRepository
) : ViewModel()

val detailModule = module {
    viewModelOf(::DetailViewModel) // route comes from parametersOf(route)

    navigation<DetailRoute> { route ->
        DetailScreen(viewModel = koinViewModel { parametersOf(route) })
    }
}
```

</TabItem>
</Tabs>

## Entry metadata

`navigation<T>` takes a `metadata` map, passed to the `NavEntry`. Use it for per-entry transitions or scene strategies such as list-detail:

```kotlin
val conversationModule = module {
    navigation<ConversationList>(metadata = ListDetailSceneStrategy.listPane()) {
        ConversationListScreen()
    }

    navigation<ConversationDetail>(metadata = ListDetailSceneStrategy.detailPane()) { route ->
        ConversationDetailScreen(route.id)
    }

    navigation<ModalRoute>(
        metadata = NavDisplay.transitionSpec {
            slideInVertically(initialOffsetY = { it }) togetherWith ExitTransition.KeepUntilTransitionsFinished
        }
    ) {
        ModalScreen()
    }
}
```

Then pass the scene strategy to `NavDisplay` (`sceneStrategy = rememberListDetailSceneStrategy()`). The metadata keys come from Navigation 3 and the adaptive libraries, not from Koin.

## From Navigation 2

| Navigation 2 | Navigation 3 with Koin |
|--------------|------------------------|
| String or typed route in `NavHost { composable(...) }` | `@Serializable` route class |
| `composable<Route> { ... }` in the `NavHost` | `navigation<Route> { ... }` in a Koin module |
| `NavHost(navController, startDestination)` | `NavDisplay(backStack, entryProvider = koinEntryProvider())` |
| Arguments read from `SavedStateHandle` | Route object passed to the lambda |

## API summary

| API | Package | Purpose |
|-----|---------|---------|
| `Module.navigation<T>(metadata) { }` | `org.koin.dsl.navigation3` | Declare an entry in a module |
| `ScopeDSL.navigation<T>(metadata) { }` | `org.koin.dsl.navigation3` | Declare an entry in a scope |
| `koinEntryProvider<T>(scope)` | `org.koin.compose.navigation3` | Entry provider from the current Compose scope |
| `entryProvider<T>()`, `getEntryProvider<T>()` | `org.koin.androidx.compose.navigation3` | Entry provider from an Activity or Fragment (Android) |

## Related

- [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel): `koinViewModel()` and its owners.
- [Android Scopes](/docs/reference/koin-android/scope): `activityRetainedScope` and Activity scopes.
- [Injected Parameters](/docs/reference/koin-core/injection-parameters): `parametersOf` and `@InjectedParam`.
- [Koin for Compose](/docs/reference/koin-compose/compose): packages and starting Koin.
- [Navigation 3 recipes](https://github.com/android/nav3-recipes): Google's samples for Navigation 3 itself.
