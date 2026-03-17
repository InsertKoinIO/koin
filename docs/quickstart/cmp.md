---
title: Compose Multiplatform - Shared UI
---

> This tutorial demonstrates a Compose Multiplatform application that displays museum art from The Metropolitan Museum of Art Collection API. It uses Koin for dependency injection across Android and iOS platforms with shared UI.
> You need around __20 min__ to complete the tutorial.

:::note
update - 2024-11-12
:::

:::tip
Looking for the **annotations version** of this tutorial? Check out [Compose Multiplatform & Annotations](./compose-multiplatform-annotations.md) which uses Koin Annotations for compile-time verification and automatic module discovery.
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/compose)
:::

## Application Overview

The application fetches museum art objects from a remote API and displays them in a list. Users can tap on an item to see detailed information:

`MuseumAPI -> MuseumStorage -> MuseumRepository -> ViewModels -> Compose UI`

**Technologies used:**
- Compose Multiplatform for shared UI (Android & iOS)
- Ktor for HTTP networking
- Koin for dependency injection
- Kotlin Coroutines & Flow for async operations
- Navigation Compose for routing

## The Data Layer

> All the common/shared code is located in `composeApp` Gradle project

### MuseumObject Model

The museum art object data class:

```kotlin
@Serializable
data class MuseumObject(
    val objectID: Int,
    val title: String,
    val artistDisplayName: String,
    val medium: String,
    val dimensions: String,
    val objectURL: String,
    val objectDate: String,
    val primaryImage: String,
    val primaryImageSmall: String,
    val repository: String,
    val department: String,
    val creditLine: String,
)
```

### MuseumApi - Network Layer

We create an API interface to fetch data from The Metropolitan Museum of Art API:

```kotlin
interface MuseumApi {
    suspend fun getData(): List<MuseumObject>
}

class KtorMuseumApi(private val client: HttpClient) : MuseumApi {
    private companion object {
        const val API_URL = "https://raw.githubusercontent.com/Kotlin/KMP-App-Template/main/list.json"
    }

    override suspend fun getData(): List<MuseumObject> {
        return try {
            client.get(API_URL).body()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            emptyList()
        }
    }
}
```

### MuseumStorage - Local Caching

We create a storage interface to cache museum objects locally:

```kotlin
interface MuseumStorage {
    suspend fun saveObjects(newObjects: List<MuseumObject>)
    fun getObjectById(objectId: Int): Flow<MuseumObject?>
    fun getObjects(): Flow<List<MuseumObject>>
}

class InMemoryMuseumStorage : MuseumStorage {
    private val storedObjects = MutableStateFlow(emptyList<MuseumObject>())

    override suspend fun saveObjects(newObjects: List<MuseumObject>) {
        storedObjects.value = newObjects
    }

    override fun getObjectById(objectId: Int): Flow<MuseumObject?> {
        return storedObjects.map { objects ->
            objects.find { it.objectID == objectId }
        }
    }

    override fun getObjects(): Flow<List<MuseumObject>> = storedObjects
}
```

### MuseumRepository

The repository coordinates between the API and storage:

```kotlin
class MuseumRepository(
    private val museumApi: MuseumApi,
    private val museumStorage: MuseumStorage,
) {
    private val scope = CoroutineScope(SupervisorJob())

    init {
        initialize()
    }

    fun initialize() {
        scope.launch {
            refresh()
        }
    }

    suspend fun refresh() {
        museumStorage.saveObjects(museumApi.getData())
    }

    fun getObjects(): Flow<List<MuseumObject>> = museumStorage.getObjects()

    fun getObjectById(objectId: Int): Flow<MuseumObject?> = museumStorage.getObjectById(objectId)
}
```

## The Shared Koin Modules

Use the `module` function to declare Koin modules. We organize our dependencies into separate modules for better structure.

:::info
This tutorial uses the **Koin Compiler Plugin DSL** (`single<T>()`, `viewModel<T>()`) which provides auto-wiring at compile time. See [Compiler Plugin Setup](/docs/setup/compiler-plugin) for configuration.
:::

### Data Module

```kotlin
val dataModule = module {
    // HttpClient for Ktor
    single { create(::buildClient) }

    // API, Storage, and Repository
    single<KtorMuseumApi>() bind MuseumApi::class
    single<InMemoryMuseumStorage>() bind MuseumStorage::class
    single<MuseumRepository>() withOptions { createdAtStart() }
}

private fun buildClient(): HttpClient {
    val json = Json { ignoreUnknownKeys = true }
    return HttpClient {
        install(ContentNegotiation) {
            json(json, contentType = ContentType.Any)
        }
    }
}
```

### ViewModel Module

Let's create ViewModels for our two screens:

```kotlin
// List screen ViewModel
class ListViewModel(museumRepository: MuseumRepository) : ViewModel() {
    val objects: StateFlow<List<MuseumObject>> =
        museumRepository.getObjects()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

// Detail screen ViewModel
class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): StateFlow<MuseumObject?> {
        return museumRepository.getObjectById(objectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }
}
```

Declare them in the ViewModel module:

```kotlin
val viewModelModule = module {
    viewModel<ListViewModel>()
    viewModel<DetailViewModel>()
}
```

### Platform-Specific Module

For platform-specific components (Android vs iOS):

```kotlin
val nativeComponentModule = module {
    single<NativeComponent>()
}
```

### Main App Module

Combine all modules:

```kotlin
val appModule = module {
    includes(dataModule, viewModelModule, nativeComponentModule)
}
```

:::note
The Koin modules are organized and can be initialized from both Android and iOS using the `initKoin()` function.
:::


## Native Component

For platform-specific information (Android vs iOS), we use an expect/actual pattern:

```kotlin
// commonMain
interface NativeComponent {
    fun getInfo(): String
}

// androidMain
class NativeComponent {
    fun getInfo(): String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

// iosMain
class NativeComponent {
    fun getInfo(): String = "iOS ${UIDevice.currentDevice.systemVersion}"
}
```

## Injecting ViewModels in Compose

> All the common Compose app is located in `commonMain` from `composeApp` Gradle module

The ViewModels are injected using `koinViewModel()` in Compose:

```kotlin
@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface {
            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = ListDestination) {
                composable<ListDestination> {
                    val vm = koinViewModel<ListViewModel>()
                    ListScreen(viewModel = vm, navigateToDetails = { objectId ->
                        navController.navigate(DetailDestination(objectId))
                    })
                }
                composable<DetailDestination> { backStackEntry ->
                    val vm = koinViewModel<DetailViewModel>()
                    DetailScreen(
                        objectId = backStackEntry.toRoute<DetailDestination>().objectId,
                        viewModel = vm,
                        navigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
```

:::info
The `koinViewModel()` function retrieves ViewModel instances and binds them to the Compose lifecycle.
:::

## Starting Koin

Initialize Koin with the `initKoin()` function:

```kotlin
fun initKoin(configuration: KoinAppDeclaration? = null) {
    startKoin {
        includes(configuration)
        modules(appModule)
    }

    val platformInfo = KoinPlatform.getKoin().get<NativeComponent>().getInfo()
    println("Running on: $platformInfo")
}
```

### Android Setup

In Android, Koin is initialized from the main Activity or Application class:

```kotlin
// Call from Android entry point
initKoin()
```

### iOS Setup

> All the iOS app is located in `iosApp` folder

In iOS, initialize Koin from the SwiftUI App entry point:

```swift
@main
struct iOSApp: App {
    init() {
        KoinKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

The Compose UI is started with:

```kotlin
fun MainViewController() = ComposeUIViewController { App() }
```
