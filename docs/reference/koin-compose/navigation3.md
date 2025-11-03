---
title: Navigation 3 Integration
---

Koin provides integration with [AndroidX Navigation 3](https://developer.android.com/jetpack/androidx/releases/navigation3) to help you build type-safe navigation graphs with dependency injection.

## Setup

Add the Navigation 3 integration dependency to your project:

### For Multiplatform Projects

```kotlin
commonMain.dependencies {
    implementation("io.insert-koin:koin-compose-navigation3:$koin_version")
}
```

### For Android-only Projects

```kotlin
dependencies {
    implementation("io.insert-koin:koin-compose-navigation3:$koin_version")
}
```

:::note
This is an **experimental API** marked with `@KoinExperimentalAPI`. The Navigation 3 library is currently in alpha.
:::

## Key Concepts

Navigation 3 integration introduces three main components:

- **`EntryProvider`** - A function that maps route objects to navigation entries
- **`EntryProviderInstaller`** - A function that registers navigation entries in Koin
- **`navigation<T>`** - A DSL function to declare navigation destinations in Koin modules

## Declaring Navigation Entries

Use the `navigation<T>()` DSL function in your Koin modules to declare navigation destinations:

### Basic Module-level Navigation

```kotlin
val appModule = module {
    single { Navigator() }
    viewModel { HomeViewModel() }
    viewModel { DetailViewModel() }

    // Declare navigation entries
    navigation<HomeRoute> { route ->
        HomeScreen(viewModel = koinViewModel())
    }

    navigation<DetailRoute> { route ->
        DetailScreen(
            viewModel = koinViewModel(),
            itemId = route.itemId
        )
    }
}

// Define your routes
@Serializable
object HomeRoute

@Serializable
data class DetailRoute(val itemId: String)
```

### Scoped Navigation

You can also declare navigation entries within Koin scopes, useful for scoping ViewModels and dependencies to specific parts of your navigation graph:

```kotlin
val appModule = module {
    // Activity scope
    activityRetainedScope {
        scoped { Navigator() }
        viewModel { ProfileViewModel() }

        navigation<ProfileRoute> { route ->
            ProfileScreen(viewModel = koinViewModel())
        }
    }

    // also with custom scope
    // Activity scope
    scope<ComponentActivity> {
        scoped { Navigator() }
        viewModel { ProfileViewModel() }

        navigation<ProfileRoute> { route ->
            ProfileScreen(viewModel = koinViewModel())
        }
    }
}
```

## Using Navigation in Compose

### Retrieving the EntryProvider

Use the `koinEntryProvider()` composable function to retrieve the aggregated navigation entries from Koin:

```kotlin
@Composable
fun App() {
    val entryProvider = koinEntryProvider()

    NavigationHost(
        entryProvider = entryProvider,
        startDestination = HomeRoute
    ) {
        // Navigation setup
    }
}
```

### With Custom Scope

You can provide a specific Koin scope to retrieve entries from:

```kotlin
@Composable
fun CheckoutFlow() {
    val checkoutScope = rememberKoinScope(named("checkout"))
    val entryProvider = koinEntryProvider(scope = checkoutScope.value)

    NavigationHost(
        entryProvider = entryProvider,
        startDestination = CheckoutRoute.Start
    ) {
        // Checkout navigation
    }
}
```

## Android-specific: ComponentCallbacks Extensions

For Android applications, you can use the `ComponentCallbacks` extensions to retrieve the entry provider from Activities or Fragments:

```kotlin
class MainActivity : ComponentActivity() {

    // Lazy initialization
    private val entryProvider by entryProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationHost(
                entryProvider = entryProvider,
                startDestination = HomeRoute
            )
        }
    }
}
```

Or use eager initialization:

```kotlin
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val entryProvider = getEntryProvider()
        setContent {
            NavigationHost(
                entryProvider = entryProvider,
                startDestination = HomeRoute
            )
        }
    }
}
```

## Accessing Route Parameters

Navigation 3 uses type-safe routes. Access route parameters directly from the route object:

```kotlin
@Serializable
data class DetailRoute(
    val itemId: String,
    val fromSearch: Boolean = false
)

val appModule = module {
    navigation<DetailRoute> { route ->
        DetailScreen(
            itemId = route.itemId,
            fromSearch = route.fromSearch,
            viewModel = koinViewModel()
        )
    }
}
```

## Combining with Koin Scopes

Navigation entries can leverage Koin's scoping capabilities:

```kotlin
val appModule = module {
    // Define a scope archetype
    scope<ComponentActivity> {
        scoped { UserSession() }

        viewModel { params ->
            UserProfileViewModel(
                userId = params.get(),
                session = get()
            )
        }

        navigation<UserProfileRoute> { route ->
            UserProfileScreen(
                viewModel = koinViewModel { parametersOf(route.userId) }
            )
        }
    }
}
```

## Complete Example

Here's a complete example showing a typical navigation setup:

```kotlin
// Define routes
@Serializable
object HomeRoute

@Serializable
object ProfileRoute

@Serializable
data class DetailRoute(val id: String)

@Serializable
data class SettingsRoute(val section: String? = null)

// Koin module
val navigationModule = module {
    // Shared dependencies
    single { ApiClient() }

    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { params -> DetailViewModel(get(), params.get()) }
    viewModel { SettingsViewModel() }

    // Navigation entries
    navigation<HomeRoute> { route ->
        HomeScreen(viewModel = koinViewModel())
    }

    navigation<ProfileRoute> { route ->
        ProfileScreen(viewModel = koinViewModel())
    }

    navigation<DetailRoute> { route ->
        DetailScreen(
            id = route.id,
            viewModel = koinViewModel { parametersOf(route.id) }
        )
    }

    navigation<SettingsRoute> { route ->
        SettingsScreen(
            initialSection = route.section,
            viewModel = koinViewModel()
        )
    }
}

// Compose app
@Composable
fun App() {
    val entryProvider = koinEntryProvider()
    val navController = rememberNavController()

    NavigationHost(
        navController = navController,
        entryProvider = entryProvider,
        startDestination = HomeRoute
    ) {
        // Navigation configuration
    }
}

// Main entry point
fun main() = application {
    KoinApplication(application = {
        modules(navigationModule)
    }) {
        App()
    }
}
```

## Migration from Navigation 2.x

If you're migrating from the Navigation 2.x integration:

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
// Define routes
@Serializable
object HomeRoute

@Serializable
data class DetailRoute(val id: String)

// Declare in module
val appModule = module {
    navigation<HomeRoute> { route ->
        HomeScreen(viewModel = koinViewModel())
    }

    navigation<DetailRoute> { route ->
        DetailScreen(id = route.id, viewModel = koinViewModel())
    }
}

// Use in app
@Composable
fun App() {
    val entryProvider = koinEntryProvider()
    val navController = rememberNavController()

    NavigationHost(
        navController = navController,
        entryProvider = entryProvider,
        startDestination = HomeRoute
    )
}
```

## API Reference

### DSL Functions

- `Module.navigation<T>(definition)` - Declares a singleton navigation entry
- `ScopeDSL.navigation<T>(definition)` - Declares a scoped navigation entry

### Composable Functions

- `koinEntryProvider(scope)` - Retrieves entry provider from given Koin scope

### Android Extensions

- `ComponentCallbacks.entryProvider()` - Lazy entry provider initialization
- `ComponentCallbacks.getEntryProvider()` - Eager entry provider initialization

## Limitations

- Navigation 3 is currently in **alpha** - API may change
- Type-safe navigation requires Kotlin serialization plugin
- Some advanced Navigation 2.x features may not be available yet

## Resources

- [AndroidX Navigation 3 Documentation](https://developer.android.com/jetpack/androidx/releases/navigation3)
- [Koin Compose Documentation](/docs/reference/koin-compose/compose)
