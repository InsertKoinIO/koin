---
title: Compose Multiplatform & Annotations - Shared UI
---

> This tutorial demonstrates a Compose Multiplatform application that displays museum art from The Metropolitan Museum of Art Collection API. It uses Koin Annotations for dependency injection across Android and iOS platforms with shared UI.
> You need around __20 min__ to complete the tutorial.

:::note
update - 2024-11-12
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/compose-annotations)
:::

## Gradle Setup

First, add the Koin annotations dependencies:

```kotlin
plugins {
    id("com.google.devtools.ksp") version kspVersion
}

dependencies {
    // Koin for Compose Multiplatform
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-compose-viewmodel:$koin_version")

    // Koin Annotations
    implementation("io.insert-koin:koin-annotations:$koin_annotations_version")
    ksp("io.insert-koin:koin-ksp-compiler:$koin_annotations_version")
}
```

## Application Overview

The application fetches museum art objects from a remote API and displays them in a list. Users can tap on an item to see detailed information:

`MuseumAPI -> MuseumStorage -> MuseumRepository -> ViewModels -> Compose UI`

**Technologies used:**
- Compose Multiplatform for shared UI (Android & iOS)
- Ktor for HTTP networking
- Koin Annotations for dependency injection
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

We create an API interface to fetch data:

```kotlin
interface MuseumApi {
    suspend fun getData(): List<MuseumObject>
}

@Single
class KtorMuseumApi(private val client: HttpClient) : MuseumApi {
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

```kotlin
interface MuseumStorage {
    suspend fun saveObjects(newObjects: List<MuseumObject>)
    fun getObjectById(objectId: Int): Flow<MuseumObject?>
    fun getObjects(): Flow<List<MuseumObject>>
}

@Single
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
@Single(createdAtStart = true)
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

:::note
The `@Single(createdAtStart = true)` annotation ensures the repository is created when Koin starts, triggering the data fetch immediately.
:::

## The Koin Modules

We organize our dependencies into separate modules:

### Data Module

```kotlin
@Module
@ComponentScan
class DataModule {

    @Single
    fun providesHttpClient(): HttpClient {
        val json = Json { ignoreUnknownKeys = true }
        return HttpClient {
            install(ContentNegotiation) {
                json(json, contentType = ContentType.Any)
            }
        }
    }
}
```

The `@ComponentScan` annotation automatically discovers all `@Single` annotated classes in this package (MuseumApi, MuseumStorage, MuseumRepository).

### ViewModel Module

Let's create ViewModels for our two screens:

```kotlin
// List screen ViewModel
@KoinViewModel
class ListViewModel(museumRepository: MuseumRepository) : ViewModel() {
    val objects: StateFlow<List<MuseumObject>> =
        museumRepository.getObjects()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

// Detail screen ViewModel
@KoinViewModel
class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): StateFlow<MuseumObject?> {
        return museumRepository.getObjectById(objectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }
}
```

Declare them in a module:

```kotlin
@ComponentScan
@Module
class ViewModelModule
```

The `@KoinViewModel` annotation automatically registers these as ViewModel definitions, and `@ComponentScan` discovers them.

### Platform-Specific Module

For platform-specific components (Android vs iOS):

```kotlin
@ComponentScan
@Module
class PlatformComponentModule
```

### Main App Module

Combine all modules:

```kotlin
@Configuration
@Module(includes = [DataModule::class, ViewModelModule::class, PlatformComponentModule::class])
class AppModule
```

* `@Configuration` - Enables automatic module discovery with `@KoinApplication`
* `@Module(includes = [...])` - Declares which modules to include

## Koin Application Object

Create a `@KoinApplication` object:

```kotlin
@KoinApplication
object KoinApp

fun initKoin(configuration: KoinAppDeclaration? = null) {
    KoinApp.startKoin {
        includes(configuration)
    }

    val platformInfo = KoinPlatform.getKoin().get<PlatformComponent>().getInfo()
    println("Running on: $platformInfo")
}
```

The `@KoinApplication` annotation generates a `startKoin()` extension function that automatically loads all modules.

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
The `koinViewModel()` function retrieves ViewModel instances automatically registered via `@KoinViewModel`.
:::

## Starting Koin

### Android Setup

In Android, Koin is initialized from the main entry point:

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

## Annotations vs Compiler Plugin DSL

Here's how the annotations approach compares to the Compiler Plugin DSL:

**With Annotations (compose-annotations/):**
```kotlin
@Configuration
@Module(includes = [DataModule::class, ViewModelModule::class])
class AppModule

@Single
class MuseumRepository(api: MuseumApi, storage: MuseumStorage)

@KoinViewModel
class ListViewModel(repository: MuseumRepository) : ViewModel()

// Start Koin
KoinApp.startKoin()
```

**Compiler Plugin DSL (compose/):**
```kotlin
val appModule = module {
    includes(dataModule, viewModelModule)
}

val dataModule = module {
    single<MuseumRepository>() withOptions { createdAtStart() }
}

val viewModelModule = module {
    viewModel<ListViewModel>()
}

// Start Koin
startKoin { modules(appModule) }
```

Both approaches achieve the same result:
- **Annotations**: Compile-time verification via KSP, automatic module discovery
- **Compiler Plugin DSL**: Auto-wiring at compile time, cleaner `single<T>()` syntax
