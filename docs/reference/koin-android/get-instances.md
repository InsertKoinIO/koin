---
title: Injecting in Android
---

Once you have declared some modules and started Koin, how can you retrieve your instances in your Android Activity, Fragments, or Services?

## Ready for Android Classes

`Activity`, `Fragment` & `Service` are extended with Koin extensions. Any `ComponentCallbacks` class has access to:

* `by inject()` - lazy evaluated instance from Koin container
* `get()` - eager fetch instance from Koin container
* `by viewModel()` - lazy ViewModel instance
* `getViewModel()` - eager ViewModel instance

## Defining Dependencies

### Compiler Plugin DSL

```kotlin
val appModule = module {
    factory<Presenter>()
    viewModel<UserViewModel>()
}
```

### Annotations

```kotlin
@Factory
class Presenter(private val repository: UserRepository)

@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

### Classic DSL

```kotlin
val appModule = module {
    factory { Presenter(get()) }
    viewModel { UserViewModel(get()) }
}
```

## Injecting in Activity

```kotlin
class DetailActivity : AppCompatActivity() {

    // Lazy inject Presenter
    private val presenter: Presenter by inject()

    // Lazy inject ViewModel
    private val viewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use presenter and viewModel
    }
}
```

## Injecting in Fragment

```kotlin
class UserFragment : Fragment() {

    // Fragment's own ViewModel
    private val viewModel: UserViewModel by viewModel()

    // Shared ViewModel with Activity
    private val sharedViewModel: SharedViewModel by activityViewModel()

    // Regular dependency
    private val presenter: Presenter by inject()
}
```

## Injecting in Service

```kotlin
class MyService : Service() {

    private val repository: UserRepository by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        repository.doSomething()
        return START_STICKY
    }
}
```

## Eager vs Lazy Injection

```kotlin
class DetailActivity : AppCompatActivity() {

    // Lazy - created on first access
    private val presenter: Presenter by inject()

    // Eager - created immediately
    private val service: MyService = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Or get eagerly in a function
        val anotherPresenter: Presenter = get()
    }
}
```

| Method | When Created | Use Case |
|--------|--------------|----------|
| `by inject()` | On first access | Most cases, avoids unnecessary creation |
| `get()` | Immediately | When you need the instance right away |

:::info
If your class doesn't have Koin extensions, implement the `KoinComponent` interface to access `inject()` or `get()`.
:::

## Injection with Parameters

Pass parameters at injection time:

```kotlin
@Factory
class UserPresenter(
    @InjectedParam val userId: String,
    val repository: UserRepository
)
```

```kotlin
class UserActivity : AppCompatActivity() {

    private val presenter: UserPresenter by inject { parametersOf("user_123") }
}
```

## Injection with Qualifiers

When you have multiple definitions of the same type:

```kotlin
val appModule = module {
    single<Database>(named("local")) { LocalDatabase() }
    single<Database>(named("remote")) { RemoteDatabase() }
}
```

```kotlin
class MyActivity : AppCompatActivity() {

    private val localDb: Database by inject(named("local"))
    private val remoteDb: Database by inject(named("remote"))
}
```

## Using Android Context in Definitions

Once your `Application` class configures Koin with `androidContext`, you can resolve it in your definitions.

### Annotations

With annotations, simply declare a `Context` or `Application` parameter - it will be automatically injected:

```kotlin
@Factory
class MyPresenter(private val context: Context)

@Singleton
class MyRepository(private val application: Application)
```

### DSL

Use `androidContext()` or `androidApplication()` functions in your module:

```kotlin
val appModule = module {
    factory {
        MyPresenter(androidContext())
    }
    single {
        MyRepository(androidApplication())
    }
}
```

## Android Scope & Context Resolution

When you have a scope binding the `Context` type, you may need to resolve `Context` from different levels:

```kotlin
class MyPresenter(val context: Context)

val appModule = module {
    scope<MyActivity> {
        scoped { MyPresenter(get()) }
    }
}
```

Context resolution:
- `get()` - resolves closest `Context`, here `MyActivity`
- `androidContext()` - resolves closest `Context`, here `MyActivity`
- `androidApplication()` - resolves `Application` from Koin setup
