---
title: WorkManager
---

Koin integrates with [Android WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) to enable constructor injection in Workers.

## Setup

### Add Dependencies

```groovy
implementation "io.insert-koin:koin-android:$koin_version"
implementation "io.insert-koin:koin-androidx-workmanager:$koin_version"
```

### Configure WorkManager

Set up the Koin WorkManager factory in your Application:

```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            workManagerFactory()
            modules(appModule)
        }
    }
}
```

### Disable Default Initializer

Add to your `AndroidManifest.xml` to disable the default WorkManager initializer:

```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="androidx.work.WorkManagerInitializer"
        android:value="androidx.startup"
        tools:node="remove" />
</provider>
```

## Declaring Workers

### Compiler Plugin DSL

```kotlin
class MyWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val myService: MyService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        myService.performTask()
        return Result.success()
    }
}

val appModule = module {
    single<MyService>()
    worker<MyWorker>()
}
```

### Annotations

```kotlin
@KoinWorker
class MyWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val myService: MyService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        myService.performTask()
        return Result.success()
    }
}

@Singleton
class MyService
```

### Classic DSL

```kotlin
val appModule = module {
    single { MyService() }
    worker { params ->
        MyWorker(
            context = params.get(),
            workerParams = params.get(),
            myService = get()
        )
    }
}
```

## Enqueuing Work

Use WorkManager normally to enqueue your worker:

```kotlin
val workRequest = OneTimeWorkRequestBuilder<MyWorker>().build()
WorkManager.getInstance(context).enqueue(workRequest)
```

## Worker with Parameters

Pass parameters via WorkManager's input data:

```kotlin
@KoinWorker
class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: DataRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val userId = inputData.getString("USER_ID") ?: return Result.failure()
        repository.syncUser(userId)
        return Result.success()
    }
}
```

Enqueue with data:

```kotlin
val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
    .setInputData(workDataOf("USER_ID" to "123"))
    .build()

WorkManager.getInstance(context).enqueue(workRequest)
```

## Quick Reference

| Approach | Declaration |
|----------|-------------|
| Compiler Plugin DSL | `worker<MyWorker>()` |
| Annotations | `@KoinWorker` |
| Classic DSL | `worker { params -> MyWorker(params.get(), params.get(), get()) }` |

| Setup | Code |
|-------|------|
| Enable factory | `workManagerFactory()` in startKoin |
| Disable default | Remove `WorkManagerInitializer` in manifest |

## Next Steps

- **[Android WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)** - Official WorkManager documentation
- **[Scopes](/docs/reference/koin-android/scope)** - Android scopes
- **[ViewModel](/docs/reference/koin-android/viewmodel)** - ViewModel injection
