---
title: WorkManager
description: Use constructor injection in WorkManager Workers with the Koin WorkerFactory and the worker definition.
---

Koin plugs a `WorkerFactory` into [Android WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager), so your Workers get their dependencies through the constructor. This page covers the setup, how to declare a Worker and how to pass input data. It assumes Koin is started in your `Application` (see [Starting Koin on Android](/docs/reference/koin-android/start)).

## Setup

Add the WorkManager artifact next to `koin-android`:

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-androidx-workmanager")
}
```

Versions come from the Koin BOM (see [Gradle Setup](/docs/setup/gradle)).

Call `workManagerFactory()` in `startKoin`, after `androidContext()`:

```kotlin
import org.koin.androidx.workmanager.koin.workManagerFactory

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

`workManagerFactory()` initializes WorkManager with a configuration that uses the Koin `WorkerFactory`. It does nothing if WorkManager is already initialized, which is the case when the default AndroidX initializer runs first. Remove that initializer in `AndroidManifest.xml`:

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

:::warning
If you keep the default initializer, WorkManager starts without the Koin factory. Workers then fail at runtime because WorkManager can't create a class with extra constructor parameters.
:::

## Declaring a Worker

A Worker takes `Context` and `WorkerParameters` from WorkManager, then any dependency from Koin:

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: SyncRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        repository.sync()
        return Result.success()
    }
}
```

Declare it as a worker:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.plugin.module.dsl.worker

val workModule = module {
    single<SyncRepository>()
    worker<SyncWorker>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.android.annotation.KoinWorker

@KoinWorker
class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: SyncRepository,
) : CoroutineWorker(context, params) {
    ...
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.dsl.workerOf

val workModule = module {
    singleOf(::SyncRepository)
    workerOf(::SyncWorker)

    // or write the constructor call
    worker { params -> SyncWorker(androidContext(), params.get(), get()) }
}
```

`WorkerParameters` is the only injected parameter: read it with `params.get()` or `get()`, and the `Context` with `androidContext()`.

</TabItem>
</Tabs>

A worker definition is a `factory` named after the Worker class and bound to `ListenableWorker`. When WorkManager asks for a Worker class, the Koin factory looks up that name. If no definition matches, Koin returns `null` and WorkManager falls back to its default factory.

## Enqueuing work

Nothing changes on the WorkManager side:

```kotlin
val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
WorkManager.getInstance(context).enqueue(request)
```

## Input data

Values that change per run go through WorkManager input data, not through Koin:

```kotlin
class UserSyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: UserRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = inputData.getString("USER_ID") ?: return Result.failure()
        repository.syncUser(userId)
        return Result.success()
    }
}
```

```kotlin
val request = OneTimeWorkRequestBuilder<UserSyncWorker>()
    .setInputData(workDataOf("USER_ID" to "123"))
    .build()

WorkManager.getInstance(context).enqueue(request)
```

Input data is stored by WorkManager and survives process death, which Koin parameters do not.

## Related

- [Starting Koin on Android](/docs/reference/koin-android/start): `startKoin` and `androidContext()`.
- [Definitions](/docs/reference/koin-core/definitions): definition types, including `worker`.
- [Android Entry Points](/docs/reference/koin-android/entry-points): hand work from a BroadcastReceiver to WorkManager.
- [Android WorkManager guide](https://developer.android.com/topic/libraries/architecture/workmanager): the WorkManager API.
