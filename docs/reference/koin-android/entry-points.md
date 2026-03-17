---
title: Android Entry Points
---

This guide covers how to inject dependencies into different Android components using Koin. Each Android component type has specific lifecycle characteristics that affect how you should use dependency injection.

:::info
This page focuses on **where** to inject (entry points). The injection APIs (`by inject()`, `get()`, `by viewModel()`) work the same regardless of how you declare definitions. For declaring definitions, see [Definitions](/docs/reference/koin-core/definitions).
:::

## Overview

Android applications consist of various component types, each with their own lifecycle and initialization patterns. Koin provides flexible ways to inject dependencies into all of them.

### Quick Reference

| Component | Injection Method | Built-in Support | Notes |
|-----------|-----------------|------------------|-------|
| **Application** | `startKoin {}` in `onCreate()` | ✅ Yes | Entry point for Koin setup |
| **Activity** | `by inject()` or `get()` | ✅ Yes | Direct injection support |
| **Fragment** | `by inject()` or `get()` | ✅ Yes | Direct injection support |
| **ViewModel** | `by viewModel()` | ✅ Yes | Lifecycle-aware injection |
| **Service** | `by inject()` or `get()` | ✅ Yes | Direct injection support |
| **BroadcastReceiver** | `KoinComponent` + `get()` | ⚠️ Manual | Implement `KoinComponent` |
| **ContentProvider** | `KoinComponent` + `get()` | ⚠️ Manual | Special timing considerations |
| **Custom View** | Constructor or `KoinComponent` | ⚠️ Manual | Consider avoiding DI |

## Application Class

The Application class is where you initialize Koin. This is the foundation for all dependency injection in your app.

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule, networkModule, dataModule)
        }
    }
}
```

:::info
For complete Application setup instructions, see [Starting Koin on Android](/docs/reference/koin-android/start).
:::

## Activity Injection

Activities have built-in Koin support through extension functions.

### Using by inject()

```kotlin
class UserActivity : AppCompatActivity() {
    // Lazy injection - created when first accessed
    private val presenter: UserPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        presenter.loadUser()  // Presenter created here
    }
}
```

### Using get()

```kotlin
class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Eager injection - created immediately
        val presenter: UserPresenter = get()
        presenter.loadUser()
    }
}
```

### With Parameters

```kotlin
class UserDetailActivity : AppCompatActivity() {
    private val userId: String by lazy { intent.getStringExtra("USER_ID") ?: "" }

    // Pass runtime parameters
    private val presenter: UserDetailPresenter by inject { parametersOf(userId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.loadUserDetails()
    }
}
```

See [Injected Parameters](/docs/reference/koin-core/definitions#injected-parameters) for declaring definitions with parameters.

:::info
For more Activity injection patterns, see [Injecting in Android](/docs/reference/koin-android/get-instances).
:::

## Fragment Injection

Fragments work identically to Activities with Koin extensions.

```kotlin
class UserListFragment : Fragment() {
    private val viewModel: UserListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadUsers()
    }
}
```

### Shared ViewModels

Share a ViewModel between Activity and Fragments:

```kotlin
class UserDetailFragment : Fragment() {
    // Get ViewModel scoped to Activity
    private val sharedViewModel: UserViewModel by activityViewModel()

    // Get ViewModel scoped to this Fragment
    private val fragmentViewModel: DetailViewModel by viewModel()
}
```

:::info
For Fragment and ViewModel injection details, see:
- [Injecting in Android](/docs/reference/koin-android/get-instances)
- [Android ViewModels](/docs/reference/koin-android/viewmodel)
:::

## Service Injection

Services have built-in Koin support through extension functions, just like Activities and Fragments.

### Using by inject()

```kotlin
class MusicPlayerService : Service() {
    // Lazy injection - created when first accessed
    private val player: MediaPlayer by inject()
    private val repository: MusicRepository by inject()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val songId = intent?.getStringExtra("SONG_ID")
        if (songId != null) {
            val song = repository.getSong(songId)
            player.play(song)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}
```

### Using get()

```kotlin
class DownloadService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Eager injection - created immediately
        val downloader: FileDownloader = get()
        val url = intent?.getStringExtra("URL") ?: ""

        downloader.start(url)
        return START_STICKY
    }
}
```

### Lifecycle Considerations

- **Services are long-lived**: Use `single` for expensive resources
- **Cleanup is critical**: Release resources in `onDestroy()`
- **Background threads**: Consider scoping background work properly

### Best Practices

```kotlin
class DownloadService : Service() {
    // Use lazy injection to delay initialization
    private val downloader: FileDownloader by inject()
    private val notificationManager: NotificationManager by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Dependencies created only when service actually starts
        downloader.start(intent?.getStringExtra("URL") ?: "")
        return START_STICKY
    }

    override fun onDestroy() {
        // Always cleanup
        downloader.cancel()
        super.onDestroy()
    }
}
```

:::note
**Alternative:** For `WorkManager` background tasks, use Koin's built-in `WorkManager` support instead of Services. See [WorkManager Integration](/docs/reference/koin-android/workmanager).
:::

## BroadcastReceiver Injection

BroadcastReceivers also need `KoinComponent` for dependency injection.

### Dynamically Registered Receiver

```kotlin
class NetworkChangeReceiver : BroadcastReceiver(), KoinComponent {
    // Use get() for eager injection (receivers are short-lived)
    private val networkMonitor: NetworkMonitor by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                networkMonitor.checkConnectivity()
            }
        }
    }
}

// Register in Activity or Service
class MainActivity : AppCompatActivity() {
    private val receiver = NetworkChangeReceiver()

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(receiver, filter)
    }

    override fun onPause() {
        unregisterReceiver(receiver)
        super.onPause()
    }
}
```

### Statically Registered Receiver (Manifest)

```kotlin
class BootReceiver : BroadcastReceiver(), KoinComponent {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Ensure Koin is initialized
            val app = context?.applicationContext as? MyApplication

            // Now safe to inject
            val scheduler: JobScheduler by inject()
            scheduler.scheduleWork()
        }
    }
}
```

```xml
<!-- AndroidManifest.xml -->
<receiver android:name=".BootReceiver"
    android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

### Important Considerations

**Lifecycle:**
- Receivers are **extremely short-lived** (typically < 10 seconds)
- Use `get()` instead of `by inject()` for faster initialization
- Avoid heavy operations in `onReceive()`

**Koin Initialization:**
- For manifest-declared receivers, ensure Koin is initialized in `Application.onCreate()`
- For dynamically registered receivers, Koin is already initialized

**Best Practices:**

```kotlin
class AlarmReceiver : BroadcastReceiver(), KoinComponent {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Use get() for immediate access (more efficient for receivers)
        val repository: AlarmRepository = get()

        // Offload work to a Service or WorkManager
        val workRequest = OneTimeWorkRequestBuilder<AlarmWorker>()
            .setInputData(workDataOf("alarm_id" to intent?.getIntExtra("ID", -1)))
            .build()

        WorkManager.getInstance(context!!).enqueue(workRequest)
    }
}
```

:::warning
BroadcastReceivers have strict time limits (~10 seconds). For any significant work, use `Service`, `WorkManager`, or `JobScheduler` instead.
:::

## ContentProvider Injection

ContentProviders have special timing considerations because they're created **before** `Application.onCreate()`.

### The Challenge

```kotlin
// ❌ PROBLEM: This won't work!
class MyContentProvider : ContentProvider(), KoinComponent {
    // Koin not initialized yet when ContentProvider is created!
    private val database: Database by inject()  // Will crash

    override fun onCreate(): Boolean {
        // This runs BEFORE Application.onCreate()
        return true
    }
}
```

### Solution 1: Lazy Initialization

```kotlin
class UserContentProvider : ContentProvider(), KoinComponent {
    // Use lazy to delay initialization
    private val database: Database by lazy {
        // Koin is ready by the time first query happens
        get<Database>()
    }

    override fun onCreate(): Boolean {
        // Don't access injected dependencies here!
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        // Safe to use database here (after Application.onCreate)
        return database.query(uri)
    }
}
```

### Solution 2: Manual Koin Initialization

```kotlin
class UserContentProvider : ContentProvider(), KoinComponent {
    private lateinit var database: Database

    override fun onCreate(): Boolean {
        // Initialize Koin if not already initialized
        val context = context ?: return false

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(context.applicationContext)
                modules(databaseModule)
            }
        }

        // Now safe to inject
        database = get()
        return true
    }

    override fun query(/* ... */): Cursor? {
        return database.query(uri)
    }
}
```

### Best Practices

```kotlin
class DataContentProvider : ContentProvider(), KoinComponent {
    // Lazy initialization pattern
    private val repository: DataRepository by lazy { get() }

    override fun onCreate(): Boolean {
        // Minimal initialization
        // Don't access Koin here
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        // Koin definitely ready by now
        return repository.getData(uri)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return repository.insert(uri, values)
    }
}
```

:::warning
**Critical:** ContentProviders are created **before** `Application.onCreate()`. Always use lazy initialization or check if Koin is initialized before injecting dependencies.
:::

## Custom View Injection

Custom Views can use dependency injection, but it should be approached carefully.

### Option 1: Constructor Injection (Recommended for Business Logic)

```kotlin
// Domain/ViewModel layer - uses constructor injection
class ChartViewModel(
    private val dataRepository: ChartDataRepository
) : ViewModel() {
    fun loadChartData(): LiveData<ChartData> {
        return dataRepository.getChartData()
    }
}

// View layer - receives data, no DI needed
class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // No dependencies injected - just draws what it's given
    fun setData(data: ChartData) {
        // Draw chart
        invalidate()
    }
}

// Activity wires them together
class ChartActivity : AppCompatActivity() {
    private val viewModel: ChartViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chartView = findViewById<ChartView>(R.id.chart)

        viewModel.loadChartData().observe(this) { data ->
            chartView.setData(data)
        }
    }
}
```

### Option 2: KoinComponent (When View Has Complex Logic)

```kotlin
class SmartChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), KoinComponent {

    // Only inject if view has significant logic
    private val chartRenderer: ChartRenderer by inject()
    private val colorScheme: ColorScheme by inject()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        chartRenderer.render(canvas, colorScheme)
    }
}
```

### When to Avoid DI in Views

❌ **Avoid injecting in Views when:**
- View is purely presentational (just draws data)
- View is used in layout preview (DI not available in preview)
- View is inflated from XML (can't pass constructor params)
- Dependencies can be passed as method parameters

✅ **Consider injecting in Views when:**
- View has complex rendering logic that's shared across app
- View needs configuration that changes per build variant
- View manages significant state or business logic (though consider moving to ViewModel)

### Best Practice: Keep Views Simple

```kotlin
// ❌ Too much logic in View
class UserCardView(context: Context) : FrameLayout(context), KoinComponent {
    private val userRepository: UserRepository by inject()
    private val imageLoader: ImageLoader by inject()

    fun loadUser(userId: String) {
        val user = userRepository.getUser(userId)  // Business logic in View!
        imageLoader.load(user.imageUrl, imageView)
    }
}

// ✅ Better: Move logic to ViewModel
class UserViewModel(
    private val userRepository: UserRepository,
    private val imageLoader: ImageLoader
) : ViewModel() {
    fun loadUser(userId: String): UserUiState {
        val user = userRepository.getUser(userId)
        return UserUiState(user, imageLoader.load(user.imageUrl))
    }
}

class UserCardView(context: Context) : FrameLayout(context) {
    // Just displays data - no DI needed
    fun bind(state: UserUiState) {
        textView.text = state.name
        imageView.setImageBitmap(state.image)
    }
}
```

:::info
**Recommendation:** Prefer keeping Views as "dumb" presentational components. Move business logic to ViewModels or Presenters where constructor injection is cleaner and more testable.
:::

## Summary

### Choosing the Right Injection Approach

| Component | Recommended Approach | Rationale |
|-----------|---------------------|-----------|
| **Application** | `startKoin {}` | Entry point - initialize Koin here |
| **Activity/Fragment** | `by inject()` or `by viewModel()` | Built-in support, clean syntax |
| **ViewModel** | Constructor injection via `viewModel {}` | Best testability |
| **Service** | `by inject()` or `get()` | Built-in support, long-lived |
| **BroadcastReceiver** | `KoinComponent` + `get()` | No built-in support, short-lived |
| **ContentProvider** | `KoinComponent` + `lazy { get() }` | Timing issues, use lazy init |
| **Custom View** | Avoid DI, pass data via methods | Keep Views simple, move logic to ViewModel |

### General Best Practices

1. **Prefer constructor injection** for business logic classes (Repositories, UseCases, ViewModels)
2. **Use field injection** (`by inject()`) for Android framework classes (Activities, Fragments, Services)
3. **Implement KoinComponent** only when necessary (BroadcastReceivers, ContentProviders, Custom Views)
4. **Use `get()` over `by inject()`** for short-lived components (BroadcastReceivers)
5. **Keep Views simple** - avoid injecting into Views when possible
6. **Watch lifecycle timing** - ContentProviders need special handling

## Next Steps

- **[Definitions](/docs/reference/koin-core/definitions)** - Declaring dependencies
- **[Injecting in Android](/docs/reference/koin-android/get-instances)** - Detailed Activity/Fragment injection
- **[Android ViewModels](/docs/reference/koin-android/viewmodel)** - ViewModel-specific patterns
- **[WorkManager Integration](/docs/reference/koin-android/workmanager)** - Background work with Koin
- **[Android Scopes](/docs/reference/koin-android/scope)** - Scope dependencies to component lifecycles
