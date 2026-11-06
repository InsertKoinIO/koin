---
title: Compose Multiplatform & Annotations
description: Build a Compose Multiplatform app for Android and iOS with Koin Annotations, a separate data module, platform modules and KoinApplication.
---

In this tutorial you build a Compose Multiplatform app for Android and iOS that lists art objects from The Metropolitan Museum of Art and shows their details. You learn how to annotate classes across two Gradle modules, provide platform-specific definitions, inject ViewModels in composables, and start Koin from your root composable. It takes about 20 minutes and needs Android Studio, plus Xcode for iOS.

This tutorial uses annotations. For the same app with the Compiler Plugin DSL, see [Compose Multiplatform](/docs/quickstart/cmp). To compare the styles, see [Choosing your style](/docs/intro/choosing-your-style).

## Get the code

The full sample is in [koin-getting-started/compose-annotations](https://github.com/InsertKoinIO/koin-getting-started/tree/main/compose-annotations). Clone the repository and open the `compose-annotations` folder in Android Studio. It has two Gradle modules: `data` for the data layer and `composeApp` for the UI. The iOS app is in `iosApp`.

## Gradle setup

Both Gradle modules apply the Koin Compiler Plugin. Versions live in a version catalog:

```toml
# gradle/libs.versions.toml
[versions]
# Kotlin, Compose Multiplatform, Ktor, navigation and Coil entries omitted
koin = "<KOIN_VERSION>"
koin-plugin = "<KOIN_PLUGIN_VERSION>"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin" }
koin-compose-viewmodel = { module = "io.insert-koin:koin-compose-viewmodel", version.ref = "koin" }

[plugins]
koin-compiler = { id = "io.insert-koin.compiler.plugin", version.ref = "koin-plugin" }
```

```kotlin
// data/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            api(libs.koin.annotations)
            ...
        }
    }
}
```

```kotlin
// composeApp/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":data"))
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.annotations)
            ...
        }
    }
}
```

The Compiler Plugin processes the annotations on every target, with no per-target setup. It needs Kotlin 2.3.20 or later, and `koin-annotations` shares the Koin version. For details, see [Gradle setup](/docs/setup/gradle) and [Compiler Plugin setup](/docs/setup/compiler-plugin).

## The application

The app fetches art objects from a remote API, caches them in memory, and shows them on a list screen and a detail screen:

```
MuseumApi -> MuseumStorage -> MuseumRepository -> ListViewModel / DetailViewModel -> Compose UI
```

It uses Ktor for HTTP, Kotlin Flow for data streams and Navigation Compose for routing. Code is in `commonMain` unless the heading or a comment says otherwise.

## Let's write the data layer

This code is in the `data` Gradle module. The `MuseumObject` data class maps the API response:

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

`KtorMuseumApi` fetches the list with a Ktor `HttpClient`. Annotate it with `@Singleton`:

```kotlin
import org.koin.core.annotation.Singleton

interface MuseumApi {
    suspend fun getData(): List<MuseumObject>
}

@Singleton
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

`InMemoryMuseumStorage` caches the objects in a `MutableStateFlow`:

```kotlin
interface MuseumStorage {
    suspend fun saveObjects(newObjects: List<MuseumObject>)
    fun getObjectById(objectId: Int): Flow<MuseumObject?>
    fun getObjects(): Flow<List<MuseumObject>>
}

@Singleton
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
@Singleton
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

Koin binds each class to its supertypes, so `KtorMuseumApi` answers requests for `MuseumApi`. See [Definitions](/docs/reference/koin-core/definitions).

## Let's declare the data module

`HttpClient` is a class you don't own, so provide it from a function in the module class:

```kotlin
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@Configuration
@ComponentScan("com.jetbrains.kmpapp.data")
class DataModule {

    @Singleton
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

`@ComponentScan` registers the annotated classes of the data package. `@Configuration` makes `DataModule` discoverable by the app entry point, even though it lives in another Gradle module. See [Modules](/docs/reference/koin-core/modules).

## Let's write the ViewModels

This code and the rest of the tutorial are in the `composeApp` Gradle module. Annotate each ViewModel with `@KoinViewModel`:

```kotlin
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ListViewModel(museumRepository: MuseumRepository) : ViewModel() {
    val objects: StateFlow<List<MuseumObject>> =
        museumRepository.getObjects()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@KoinViewModel
class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)
}
```

Scan them with a module class in the `screens` package:

```kotlin
@ComponentScan
@Module
class ViewModelModule
```

`@ComponentScan` with no argument scans the package of the module class and its subpackages. See [ViewModel](/docs/reference/koin-core/viewmodel).

## Let's add platform-specific definitions

The sample shows two ways to provide a different implementation per platform. The first is an annotated `expect` class, scanned by a shared module:

```kotlin
// commonMain
@Singleton
expect class NativeComponent() {
    fun getInfo(): String
}

@Module
@ComponentScan
class NativeComponentModule
```

```kotlin
// androidMain
@Singleton
actual class NativeComponent actual constructor() {
    actual fun getInfo(): String = "Android NativeComponent"
}
```

```kotlin
// nativeMain
@Singleton
actual class NativeComponent actual constructor() {
    actual fun getInfo(): String = "iOS NativeComponent"
}
```

The second is an `expect` module class. Each platform's `actual` module scans its own implementation of a shared interface:

```kotlin
// commonMain
interface PlatformComponent {
    fun getInfo(): String
}

@Module
expect class PlatformComponentModule
```

```kotlin
// androidMain
@Module
@ComponentScan
actual class PlatformComponentModule

@Singleton
class AndroidPlatformComponent : PlatformComponent {
    override fun getInfo(): String = "Android PlatformComponent"
}
```

```kotlin
// iosMain
@Module
@ComponentScan
actual class PlatformComponentModule

@Singleton
class IOSPlatformComponent : PlatformComponent {
    override fun getInfo(): String = "iOS PlatformComponent"
}
```

Platform classes can also take platform dependencies. On Android, `AndroidContextWrapper` receives the Android `Context` from Koin:

```kotlin
// commonMain
interface ContextWrapper {
    fun getContextInfo(): String
}

@Singleton
class NativeContextComponent(val ctx: ContextWrapper) {
    fun getInfo(): String = "NativeContextComponent with ctx: ${ctx.getContextInfo()}"
}
```

```kotlin
// androidMain
@Singleton
class AndroidContextWrapper(val context: Context) : ContextWrapper {
    override fun getContextInfo(): String = "Android Context: $context"
}
```

```kotlin
// nativeMain
@Singleton
class IOSContextWrapper : ContextWrapper {
    override fun getContextInfo(): String = "iOS Context"
}
```

Shared code asks for the interface and gets the right implementation on each platform. These patterns are covered in [KMP shared modules](/docs/reference/koin-core/kmp-shared-modules).

## Let's assemble the modules

Declare the app module with its includes, and a `@KoinApplication` object as the entry point:

```kotlin
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module

@Module(includes = [ViewModelModule::class, NativeComponentModule::class, PlatformComponentModule::class])
@Configuration
class AppModule

@KoinApplication
object KoinApp
```

`KoinApp` collects every `@Configuration` module: `AppModule` with its includes, and `DataModule` from the `data` Gradle module. The Compiler Plugin validates the full graph at build time.

## Let's start Koin and inject the ViewModels

Start Koin from the root composable with `KoinApplication` and `koinConfiguration<KoinApp>()`:

```kotlin
import org.koin.compose.KoinApplication
import org.koin.plugin.module.dsl.koinConfiguration

@Serializable
object ListDestination

@Serializable
data class DetailDestination(val objectId: Int)

@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration<KoinApp>()) {
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
}
```

Each screen gets its ViewModel with `koinViewModel()`:

```kotlin
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ListScreen(navigateToDetails: (objectId: Int) -> Unit) {
    val viewModel = koinViewModel<ListViewModel>()
    val objects by viewModel.objects.collectAsStateWithLifecycle()
    ...
}

@Composable
fun DetailScreen(objectId: Int, navigateBack: () -> Unit) {
    val viewModel = koinViewModel<DetailViewModel>()
    val obj by viewModel.getObject(objectId).collectAsStateWithLifecycle(initialValue = null)
    ...
}
```

`KoinApplication` starts Koin once for the whole app. On Android it also sets the Android context and logger, which is how `AndroidContextWrapper` gets its `Context`. Both platforms host the same `App` composable, so there is no separate start code in `Application` or in the iOS app:

```kotlin
// iosMain
fun MainViewController() = ComposeUIViewController { App() }
```

Run the Android app from Android Studio, or the iOS app from Xcode, and the list of art objects appears. Outside Compose, use `startKoin<KoinApp>()` from `org.koin.plugin.module.dsl` instead. See [Koin for Compose](/docs/reference/koin-compose/compose) and [Compose ViewModel](/docs/reference/koin-compose/compose-viewmodel).

## Next steps

- [Core Concepts](/docs/intro/core-concepts): the mental model behind modules, definitions and injection.
- [KMP shared modules](/docs/reference/koin-core/kmp-shared-modules): share and split Koin modules across platforms.
- [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory): every annotation and its parameters.
- [Compose testing](/docs/reference/koin-compose/compose-testing): test composables that use Koin.
- [Tooling](/docs/tooling): IDE plugin and monitoring tools for Koin.
