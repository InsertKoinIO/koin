---
title: Navigation 3
---

# Navigation 3

Koin provides integration with [AndroidX Navigation 3](https://developer.android.com/guide/navigation/navigation-3) for type-safe, multiplatform navigation with dependency injection.

## What is Navigation 3?

Navigation 3 is Jetpack's new navigation library designed specifically for Compose:

- **Full back stack control** - Navigate by adding/removing items from a list
- **Type-safe routes** - Routes are Kotlin classes with `@Serializable`
- **Adaptive layouts** - Display multiple destinations simultaneously (list-detail)
- **Automatic animations** - Built-in transition support

## Setup

### Multiplatform Projects

```kotlin
// shared/build.gradle.kts
commonMain.dependencies {
    implementation("io.insert-koin:koin-compose-navigation3:$koin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization_version")
}
```

### Android-only Projects

```kotlin
dependencies {
    implementation("io.insert-koin:koin-compose-navigation3:$koin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization_version")
}
```

Apply the serialization plugin:

```kotlin
plugins {
    kotlin("plugin.serialization")
}
```

### Platform Support

| Platform | Status |
|----------|--------|
| Android | Full support |
| iOS | Full support |
| Desktop | Full support |
| Web | Full support |

## Core Concepts

### Routes as Kotlin Classes

Define type-safe routes using `@Serializable`:

```kotlin
@Serializable
data object HomeRoute

@Serializable
data object ProfileRoute

@Serializable
data class DetailRoute(val itemId: String)

@Serializable
data class SettingsRoute(val section: String? = null)
```

### Back Stack

Navigation 3 uses a simple list-based back stack:

```kotlin
// Basic back stack
val backStack = remember { mutableStateListOf<Any>(HomeRoute) }

// Persistent back stack (survives config changes)
val backStack = rememberNavBackStack(HomeRoute)

// Navigate forward
backStack.add(DetailRoute("123"))

// Navigate back
backStack.removeLastOrNull()
```

### NavDisplay

`NavDisplay` renders the back stack with animations:

```kotlin
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = { route -> /* NavEntry */ }
)
```

## Koin Integration

### Declaring Navigation Entries

Use the `navigation<T>` DSL in your modules:

```kotlin
val appModule = module {
    // Dependencies
    single<ApiClient>()
    viewModel<HomeViewModel>()
    viewModel<DetailViewModel>()

    // Navigation entries with Koin injection
    navigation<HomeRoute> { route ->
        HomeScreen(viewModel = koinViewModel())
    }

    navigation<DetailRoute> { route ->
        DetailScreen(
            itemId = route.itemId,
            viewModel = koinViewModel { parametersOf(route.itemId) }
        )
    }

    navigation<ProfileRoute> { route ->
        ProfileScreen(viewModel = koinViewModel())
    }
}
```

### Using koinEntryProvider

Retrieve all navigation entries from Koin:

```kotlin
@Composable
fun App() {
    val backStack = rememberNavBackStack(HomeRoute)
    val entryProvider = koinEntryProvider<Any>()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider
    )
}
```

### Complete Example

```kotlin
// Routes
@Serializable data object ConversationList
@Serializable data class ConversationDetail(val id: Int)
@Serializable data object Profile

// Navigator class for cleaner navigation
class Navigator(startDestination: Any) {
    val backStack = mutableStateListOf(startDestination)

    fun goTo(destination: Any) {
        backStack.add(destination)
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}

// Koin modules
val appModule = module {
    includes(conversationModule, profileModule)

    activityRetainedScope {
        scoped { Navigator(startDestination = ConversationList) }
    }
}

val conversationModule = module {
    activityRetainedScope {
        navigation<ConversationList> {
            val navigator = get<Navigator>()
            ConversationListScreen(
                onConversationClicked = { detail ->
                    navigator.goTo(detail)
                }
            )
        }

        navigation<ConversationDetail> { route ->
            val navigator = get<Navigator>()
            ConversationDetailScreen(
                conversationId = route.id,
                onProfileClicked = { navigator.goTo(Profile) }
            )
        }
    }
}

val profileModule = module {
    activityRetainedScope {
        navigation<Profile> {
            ProfileScreen()
        }
    }
}

// Activity
class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navigator: Navigator = get()

            Scaffold { padding ->
                NavDisplay(
                    backStack = navigator.backStack,
                    modifier = Modifier.padding(padding),
                    onBack = { navigator.goBack() },
                    entryProvider = getEntryProvider()
                )
            }
        }
    }
}
```

## Scoped Navigation

Declare navigation entries within Koin scopes:

```kotlin
val appModule = module {
    // Activity-retained scope (survives config changes)
    activityRetainedScope {
        scoped { UserSession() }
        viewModel<ProfileViewModel>()

        navigation<ProfileRoute> { route ->
            ProfileScreen(viewModel = koinViewModel())
        }
    }

    // Custom scope
    scope<CheckoutFlow> {
        scoped { CheckoutState() }
        viewModel<CheckoutViewModel>()

        navigation<CartRoute> { route ->
            CartScreen(viewModel = koinViewModel())
        }

        navigation<PaymentRoute> { route ->
            PaymentScreen(viewModel = koinViewModel())
        }
    }
}
```

## ViewModel Integration

### With Navigation Arguments

Pass route data to ViewModels:

```kotlin
@Serializable
data class DetailRoute(val itemId: String, val fromSearch: Boolean = false)

class DetailViewModel(
    val route: DetailRoute,
    private val repository: Repository
) : ViewModel() {
    val item = repository.getItem(route.itemId)
}

val appModule = module {
    viewModelOf(::DetailViewModel)

    navigation<DetailRoute> { route ->
        DetailScreen(
            viewModel = koinViewModel { parametersOf(route) }
        )
    }
}
```

### With Entry Decorators

Use decorators for ViewModel state retention:

```kotlin
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator()
    ),
    entryProvider = entryProvider {
        entry<DetailRoute> { route ->
            val viewModel = koinViewModel<DetailViewModel> {
                parametersOf(route)
            }
            DetailScreen(viewModel)
        }
    }
)
```

## Animations

### Default Transitions

```kotlin
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = entryProvider,
    // Forward navigation animation
    transitionSpec = {
        slideInHorizontally(initialOffsetX = { it }) togetherWith
        slideOutHorizontally(targetOffsetX = { -it })
    },
    // Back navigation animation
    popTransitionSpec = {
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
        slideOutHorizontally(targetOffsetX = { it })
    }
)
```

### Per-Route Animations

```kotlin
navigation<ModalRoute>(
    metadata = NavDisplay.transitionSpec {
        slideInVertically(initialOffsetY = { it }) togetherWith
        ExitTransition.KeepUntilTransitionsFinished
    } + NavDisplay.popTransitionSpec {
        EnterTransition.None togetherWith
        slideOutVertically(targetOffsetY = { it })
    }
) { route ->
    ModalScreen()
}
```

## Adaptive Layouts

### List-Detail Pattern

Use scene strategies for adaptive layouts:

```kotlin
@Composable
fun App() {
    val backStack = rememberNavBackStack(ConversationList)
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategy = listDetailStrategy,
        entryProvider = entryProvider {
            entry<ConversationList>(
                metadata = ListDetailSceneStrategy.listPane()
            ) {
                ConversationListScreen()
            }

            entry<ConversationDetail>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { route ->
                ConversationDetailScreen(route.id)
            }
        }
    )
}
```

### With Koin Modules

```kotlin
val appModule = module {
    navigation<ConversationList>(
        metadata = ListDetailSceneStrategy.listPane()
    ) {
        ConversationListScreen(
            onItemClick = { get<Navigator>().goTo(it) }
        )
    }

    navigation<ConversationDetail>(
        metadata = ListDetailSceneStrategy.detailPane()
    ) { route ->
        ConversationDetailScreen(route.id)
    }
}
```

## Android Extensions

### Lazy Entry Provider

```kotlin
class MainActivity : ComponentActivity() {
    // Lazy initialization
    private val entryProvider by entryProvider<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val backStack = rememberNavBackStack(HomeRoute)

            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider
            )
        }
    }
}
```

### Eager Entry Provider

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val entryProvider = getEntryProvider<Any>()

        setContent {
            NavDisplay(
                backStack = backStack,
                entryProvider = entryProvider,
                onBack = { backStack.removeLastOrNull() }
            )
        }
    }
}
```

## API Reference

### DSL Functions

| Function | Description |
|----------|-------------|
| `Module.navigation<T> { }` | Declare navigation entry at module level |
| `ScopeDSL.navigation<T> { }` | Declare navigation entry within a scope |

### Composable Functions

| Function | Description |
|----------|-------------|
| `koinEntryProvider<T>()` | Get aggregated entry provider from Koin |

### Android Extensions

| Function | Description |
|----------|-------------|
| `entryProvider<T>()` | Lazy entry provider delegate |
| `getEntryProvider<T>()` | Eager entry provider |

## Migration from Navigation 2.x

### Before (Navigation 2.x)

```kotlin
NavHost(navController, startDestination = "home") {
    composable("home") {
        HomeScreen(viewModel = koinViewModel())
    }
    composable("detail/{id}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id")
        DetailScreen(id = id, viewModel = koinViewModel())
    }
}
```

### After (Navigation 3)

```kotlin
// Type-safe routes
@Serializable data object HomeRoute
@Serializable data class DetailRoute(val id: String)

// Module declaration
val appModule = module {
    navigation<HomeRoute> { HomeScreen(viewModel = koinViewModel()) }
    navigation<DetailRoute> { route ->
        DetailScreen(id = route.id, viewModel = koinViewModel())
    }
}

// Usage
val backStack = rememberNavBackStack(HomeRoute)
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = koinEntryProvider()
)
```

## Resources

- [Navigation 3 Official Guide](https://developer.android.com/guide/navigation/navigation-3)
- [Nav3 Recipes Repository](https://github.com/android/nav3-recipes)
- [Koin Compose Documentation](/docs/reference/koin-compose/compose)
