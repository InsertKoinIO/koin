---
title: Compose Multiplatform
description: Build a Compose Multiplatform app for Android and iOS with Koin and the Compiler Plugin DSL, with shared modules, platform modules and ViewModels.
---

In this tutorial you build a Compose Multiplatform app for Android and iOS that lists art objects from The Metropolitan Museum of Art and shows their details. You learn how to share Koin modules in `commonMain`, provide platform-specific definitions, inject ViewModels in composables, and start Koin on both platforms. It takes about 20 minutes and needs Android Studio, plus Xcode for iOS.

This tutorial uses the Compiler Plugin DSL. For the same app with annotations, see [Compose Multiplatform & Annotations](/docs/quickstart/compose-multiplatform-annotations). To compare the styles, see [Choosing your style](/docs/intro/choosing-your-style).

## Get the code

The full sample is in [koin-getting-started/compose](https://github.com/InsertKoinIO/koin-getting-started/tree/main/compose). Clone the repository and open the `compose` folder in Android Studio. The shared code is in the `composeApp` Gradle module, and the iOS app is in `iosApp`.

## Gradle setup

The sample applies the Koin Compiler Plugin and adds `koin-core` and `koin-compose-viewmodel-navigation` to `commonMain`. Versions live in a version catalog:

```toml
# gradle/libs.versions.toml
[versions]
# Kotlin, Compose Multiplatform, Ktor, navigation and Coil entries omitted
koin = "<KOIN_VERSION>"
koin-plugin = "<KOIN_PLUGIN_VERSION>"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose-viewmodel-navigation = { module = "io.insert-koin:koin-compose-viewmodel-navigation", version.ref = "koin" }

[plugins]
koin-compiler = { id = "io.insert-koin.compiler.plugin", version.ref = "koin-plugin" }
```

```kotlin
// composeApp/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel.navigation)
            ...
        }
    }
}
```

The Compiler Plugin needs Kotlin 2.3.20 or later and runs on every target. For current versions, see [Gradle setup](/docs/setup/gradle). For the multiplatform project layout, see [KMP setup](/docs/reference/koin-core/kmp-setup).

## The application

The app fetches art objects from a remote API, caches them in memory, and shows them on a list screen and a detail screen:

```
MuseumApi -> MuseumStorage -> MuseumRepository -> ListViewModel / DetailViewModel -> Compose UI
```

It uses Ktor for HTTP, Kotlin Flow for data streams and Navigation Compose for routing. All the code in this tutorial is in `commonMain` unless the heading says otherwise.

## Let's write the data layer

The `MuseumObject` data class maps the API response:

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

`MuseumApi` fetches the list with a Ktor `HttpClient`:

```kotlin
interface MuseumApi {
    suspend fun getData(): List<MuseumObject>
}

class KtorMuseumApi(private val client: HttpClient) : MuseumApi {
    companion object {
        private const val API_URL =
            "https://raw.githubusercontent.com/Kotlin/KMP-App-Template/main/list.json"
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

`MuseumStorage` caches the objects in a `MutableStateFlow`:

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

`MuseumRepository` refreshes the storage from the API when it is created:

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

Each class takes its dependencies in its constructor, so Koin can wire all of them.

## Let's declare the data module

A module is where you declare the components Koin creates (see [Core Concepts](/docs/intro/core-concepts)). Declare the data layer in `dataModule`:

```kotlin
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create
import org.koin.plugin.module.dsl.single

val dataModule = module {
    single { create(::buildClient) }
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

`create(::buildClient)` lets Koin call a builder function for a class you don't own, here `HttpClient`. `createdAtStart()` creates `MuseumRepository` when Koin starts, so the data fetch begins right away. Both are described in [Definitions](/docs/reference/koin-core/definitions).

## Let's write the ViewModels

The list screen and the detail screen each get a ViewModel:

```kotlin
class ListViewModel(museumRepository: MuseumRepository) : ViewModel() {
    val objects: StateFlow<List<MuseumObject>> =
        museumRepository.getObjects()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)
}
```

Declare them in `viewModelModule`:

```kotlin
import org.koin.plugin.module.dsl.viewModel

val viewModelModule = module {
    viewModel<ListViewModel>()
    viewModel<DetailViewModel>()
}
```

Koin creates each ViewModel through the multiplatform ViewModel API, so the same declaration works on Android and iOS. See [ViewModel](/docs/reference/koin-core/viewmodel).

## Let's add platform-specific definitions

The sample shows two ways to provide a different implementation per platform. The first is an `expect` class, declared once in a shared module:

```kotlin
// commonMain
expect class NativeComponent() {
    fun getInfo(): String
}

val nativeComponentModule = module {
    single<NativeComponent>()
}
```

```kotlin
// androidMain
actual class NativeComponent actual constructor() {
    actual fun getInfo(): String = "Android OS"
}
```

```kotlin
// nativeMain
actual class NativeComponent actual constructor() {
    actual fun getInfo(): String = "iOS"
}
```

The second is an `expect` module function. Each platform returns its own module, binding its implementation of a shared interface:

```kotlin
// commonMain
interface PlatformComponent {
    fun getInfo(): String
}

expect fun platformModule(): Module
```

```kotlin
// androidMain
class AndroidComponent() : PlatformComponent {
    override fun getInfo(): String = "Android OS"
}

actual fun platformModule(): Module = module {
    single<AndroidComponent>() bind PlatformComponent::class
}
```

```kotlin
// iosMain
class IOSComponent() : PlatformComponent {
    override fun getInfo(): String = "iOS OS"
}

actual fun platformModule(): Module = module {
    single<IOSComponent>() bind PlatformComponent::class
}
```

Shared code asks for `NativeComponent` or `PlatformComponent` and gets the right implementation on each platform. These patterns are covered in [KMP shared modules](/docs/reference/koin-core/kmp-shared-modules).

## Let's assemble the modules

Gather all modules in `appModule` with `includes()`:

```kotlin
val appModule = module {
    includes(dataModule, viewModelModule, nativeComponentModule, platformModule())
}
```

You now load a single module at start, and it brings the others. See [Modules](/docs/reference/koin-core/modules).

## Let's inject the ViewModels in Compose

Each screen gets its ViewModel with `koinViewModel()` as a default parameter:

```kotlin
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ListScreen(
    viewModel: ListViewModel = koinViewModel(),
    navigateToDetails: (objectId: Int) -> Unit
) {
    val objects by viewModel.objects.collectAsStateWithLifecycle()
    ...
}

@Composable
fun DetailScreen(
    objectId: Int,
    viewModel: DetailViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val obj by viewModel.getObject(objectId).collectAsStateWithLifecycle(initialValue = null)
    ...
}
```

The `App` composable sets up navigation between the two screens:

```kotlin
@Serializable
object ListDestination

@Serializable
data class DetailDestination(val objectId: Int)

@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface {
            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = ListDestination) {
                composable<ListDestination> {
                    ListScreen(navigateToDetails = { objectId ->
                        navController.navigate(DetailDestination(objectId))
                    })
                }
                composable<DetailDestination> { backStackEntry ->
                    DetailScreen(
                        objectId = backStackEntry.toRoute<DetailDestination>().objectId,
                        navigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
```

Inside a `NavHost` destination, `koinViewModel()` ties the ViewModel to that navigation entry, so each screen keeps its own instance. See [Compose ViewModel](/docs/reference/koin-compose/compose-viewmodel).

## Let's start Koin

Write a shared `initKoin()` function that both platforms call. It accepts an optional configuration for platform-specific setup:

```kotlin
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

fun initKoin(configuration: KoinAppDeclaration? = null) {
    startKoin {
        includes(configuration)
        modules(appModule)
        printLogger(Level.DEBUG)
    }
}
```

On Android, call it from your `Application` class and pass the Android context:

```kotlin
// androidMain
class MuseumApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MuseumApp)
        }
    }
}
```

On iOS, call it from the SwiftUI app entry point. Kotlin exports `initKoin()` as `doInitKoin()`:

```swift
// iosApp
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

The iOS view controller hosts the same `App` composable:

```kotlin
// iosMain
fun MainViewController() = ComposeUIViewController { App() }
```

Run the Android app from Android Studio, or the iOS app from Xcode, and the list of art objects appears. See [Starting Koin](/docs/reference/koin-core/starting-koin) for the other start options.

## Next steps

- [Core Concepts](/docs/intro/core-concepts): the mental model behind modules, definitions and injection.
- [KMP setup](/docs/reference/koin-core/kmp-setup): project layout and Koin dependencies for Kotlin Multiplatform.
- [Koin for Compose](/docs/reference/koin-compose/compose): `KoinApplication`, `koinInject()` and Compose setup.
- [Compose testing](/docs/reference/koin-compose/compose-testing): test composables that use Koin.
- [Tooling](/docs/tooling): IDE plugin and monitoring tools for Koin.
