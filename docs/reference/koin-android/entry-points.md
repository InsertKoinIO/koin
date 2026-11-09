---
title: Android Entry Points
description: Inject dependencies in Activity, Fragment, Service, BroadcastReceiver, ContentProvider, custom View and the Application class.
---

Android creates its components itself (Activity, Fragment, Service, BroadcastReceiver), so Koin can't use constructor injection for them. These classes are entry points: they ask Koin for their dependencies, and everything below them uses constructor injection. This page shows how to do it in each Android component. It assumes Koin is started (see [Starting Koin on Android](/docs/reference/koin-android/start)) and your definitions are declared (see [Definitions](/docs/reference/koin-core/definitions)).

The injection code is the same whichever style you use to declare definitions.

## Components at a glance

| Component | How to inject | Koin extensions built in |
|-----------|---------------|--------------------------|
| `Application` | `by inject()`, after `startKoin` | Yes |
| `Activity` | `by inject()`, `get()`, `by viewModel()` | Yes |
| `Fragment` | `by inject()`, `get()`, `by viewModel()`, `by activityViewModel()` | Yes |
| `Service` | `by inject()`, `get()` | Yes |
| `ContentProvider` | `by inject()`, with timing limits | Yes |
| `BroadcastReceiver` | Implement `KoinComponent` | No |
| Custom `View` | Implement `KoinComponent`, or pass data in | No |

Koin adds `inject()` and `get()` to every Android `ComponentCallbacks` class: `Application`, `Activity`, `Fragment`, `Service` and `ContentProvider`. Other framework-created classes (receivers, Views inflated from XML) implement [`KoinComponent`](/docs/reference/koin-core/koin-component) to get the same functions. Your own classes (repositories, use cases, ViewModels) never do: they receive dependencies through their constructor.

## Activity

```kotlin
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserActivity : AppCompatActivity() {

    private val presenter: UserPresenter by inject()
    private val viewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.loadUser()
    }
}
```

`by viewModel()` is not the same as `by inject()`: it goes through the Android `ViewModelStore`, so the instance survives rotation. Use it for every ViewModel. See [Android ViewModel](/docs/reference/koin-android/viewmodel).

## Fragment

```kotlin
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserFragment : Fragment() {

    private val presenter: UserPresenter by inject()
    private val viewModel: UserDetailViewModel by viewModel()
    private val sharedViewModel: UserViewModel by activityViewModel()
}
```

`by viewModel()` gives a ViewModel owned by the Fragment. `by activityViewModel()` gives the one owned by the host Activity, shared with the Activity and its other Fragments.

To create Fragments with constructor injection instead, see [Fragment Factory](/docs/reference/koin-android/fragment-factory).

## Service

```kotlin
class MusicPlayerService : Service() {

    private val player: MusicPlayer by inject()
    private val repository: MusicRepository by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra("SONG_ID")?.let { songId ->
            player.play(repository.getSong(songId))
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}
```

A Service can live a long time. Release what it holds in `onDestroy()`. For instances that should live exactly as long as the Service, extend `ScopeService` (see [Android Scopes](/docs/reference/koin-android/scope#service-scope)). For deferred background work, prefer [WorkManager](/docs/reference/koin-android/workmanager), which supports constructor injection in Workers.

## BroadcastReceiver

Android creates receivers itself, with an empty constructor, and `BroadcastReceiver` has no Koin extensions. Implement `KoinComponent` to make it an entry point:

```kotlin
class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val scheduler: SyncScheduler by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduler.scheduleSync()
        }
    }
}
```

Koin is ready in `onReceive()`, for receivers declared in the manifest and for receivers registered at runtime: Android creates the `Application`, and runs `startKoin`, before it delivers a broadcast.

`onReceive()` runs on the main thread and has about 10 seconds before the system considers the receiver blocked. Keep it short and hand long work to WorkManager:

```kotlin
class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val workManager: WorkManager by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val request = OneTimeWorkRequestBuilder<AlarmWorker>()
            .setInputData(workDataOf("alarm_id" to intent.getIntExtra("ID", -1)))
            .build()
        workManager.enqueue(request)
    }
}
```

This assumes a `WorkManager` definition in your modules, for example `single { WorkManager.getInstance(androidContext()) }`.

## ContentProvider

`ContentProvider` has Koin extensions, but Android creates it before `Application.onCreate()`. At that time `startKoin` has not run yet.

```kotlin
class UserContentProvider : ContentProvider() {

    private val repository: UserRepository by inject()

    override fun onCreate(): Boolean {
        // Koin is not started yet: don't touch repository here
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = repository.query(uri)

    ...
}
```

`by inject()` is lazy, so the instance is resolved on the first `query()`, after `Application.onCreate()`. Calling `get()`, or reading `repository`, in `onCreate()` fails with "KoinApplication has not been started".

If the provider needs Koin in its own `onCreate()`, Koin must start before it. Start Koin there, guarded so it runs once, and remove `startKoin` from your `Application`:

```kotlin
override fun onCreate(): Boolean {
    val appContext = context?.applicationContext ?: return false
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            androidContext(appContext)
            modules(appModule)
        }
    }
    return true
}
```

:::warning
Two calls to `startKoin` in the same process fail with `KoinApplicationAlreadyStartedException`. If you start Koin from a ContentProvider, the `Application` class must not start it again.
:::

## Custom View

A View should display data, not fetch it. Most Views need no injection: the Activity or Fragment injects a ViewModel and passes the data to the View.

```kotlin
class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    fun setData(data: ChartData) {
        ...
        invalidate()
    }
}
```

When a View inflated from XML needs a shared helper (a renderer, an image loader), Android calls its constructor, so Koin can't pass the helper in. Implement `KoinComponent`:

```kotlin
class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatImageView(context, attrs), KoinComponent {

    private val imageLoader: ImageLoader by inject()

    fun load(url: String) {
        if (!isInEditMode) imageLoader.load(url, this)
    }
}
```

Koin is not started in the Android Studio layout preview. Don't resolve dependencies when `isInEditMode` is true.

## Application class

The `Application` class has Koin extensions too. Use them after `startKoin`:

```kotlin
class MainApplication : Application() {

    private val crashReporter: CrashReporter by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
        crashReporter.install()
    }
}
```

## Lazy or eager

```kotlin
class UserActivity : AppCompatActivity() {

    // lazy: resolved on first access
    private val presenter: UserPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // eager: resolved now
        val analytics: Analytics = get()
    }
}
```

| Function | Resolved | Use it for |
|----------|----------|------------|
| `by inject()` | On first access of the property | Properties: nothing is created until you use it |
| `get()` | When called | Local values inside a function |

A `get()` in a property initializer runs when Android constructs the Activity or Fragment, before `onCreate()`, and creates the instance even if the screen never uses it. Prefer `by inject()` for properties. The lazy delegate is thread safe by default (`LazyThreadSafetyMode.SYNCHRONIZED`); pass `inject(mode = LazyThreadSafetyMode.NONE)` when the property is only read on the main thread.

How `inject()` and `get()` work on every platform is explained in [Injection](/docs/reference/koin-core/injection).

## Parameters and qualifiers at the call site

Both functions take a [qualifier](/docs/reference/koin-core/qualifiers) and [injected parameters](/docs/reference/koin-core/injection-parameters):

```kotlin
class UserDetailActivity : AppCompatActivity() {

    private val userId: String by lazy { intent.getStringExtra("USER_ID").orEmpty() }

    private val presenter: UserDetailPresenter by inject { parametersOf(userId) }
    private val localDatabase: Database by inject(named("local"))
}
```

The parameter lambda runs when the property is first read. Reading `intent` there is safe, because it happens after `onCreate()`.

## Related

- [Android ViewModel](/docs/reference/koin-android/viewmodel): `by viewModel()`, `activityViewModel()` and navigation graph ViewModels.
- [Android Scopes](/docs/reference/koin-android/scope): instances bound to an Activity, Fragment or Service.
- [Injection](/docs/reference/koin-core/injection): `inject()`, `get()`, nullable and lazy injection on every platform.
- [KoinComponent](/docs/reference/koin-core/koin-component): when and how to use it outside Android components.
- [WorkManager](/docs/reference/koin-android/workmanager): constructor injection in Workers.
