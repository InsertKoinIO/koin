The `koin-androidx-workmanager` project is dedicated to bring Android WorkManager features.

## WorkManager DSL

## Setup WorkManager

At start, in your KoinApplication declaration, use the `workManagerFactory()` keyword to a setup custom WorkManager instance:

```kotlin
class MainApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // setup a WorkManager instance
            workManagerFactory()
            modules(...)
        }

        setupWorkManagerFactory()
}
```

It's also important that you edit your AndroidManifest.xml to prevent
Android initializing its default WorkManagerFactory, as shown in https://developer.android.com/topic/libraries/architecture/workmanager/advanced/custom-configuration
. Failing to do so will make the app crash.


```xml
    <application . . .>
        . . .
        <provider
            android:name="androidx.work.impl.WorkManagerInitializer"
            android:authorities="${applicationId}.workmanager-init"
            tools:node="remove" />
    </application
```

## Declare ListenableWorker

```kotlin
val appModule = module {
    single { MyService() }
    worker { MyListenableWorker(get(), get()) }
}
```



### Creating extra work manager factories

You can also write a WorkManagerFactory and hand it over to Koin. It will be added as a delegate.

```kotlin
class MainApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        startKoin {
           workManagerFactory(workFactory1, workFactory2)
           . . .
        }

        setupWorkManagerFactory()
    }
}

```

In case both Koin and workFactory1 provided WorkManagerFactory can instantiate a ListenableWorker, the factory provided by Koin will be the one used.

## A few assumptions

### Add manifest changes in koin lib itself
We can make it one step less for application developers if koin-androidx-workmanager's own manifest disables the default work manager. However it can be confusing since if the app developer don't initialize koin's work manager infrastructure, he'll end up having no usable work manager factories.


That's something that checkModules could help: if any class in the project implements ListenableWorker we inspect both manifest and code and make sure they make sense?

### DSL Improvement option:
```kotlin

val workerFactoryModule = module {
   factory<WorkFactory> { WorkFactory1() }
   factory<WorkFactory> { WorkFactory2() }
}

}
```

then have koin internals do something like

```kotlin
fun Application.setupWorkManagerFactory(
  // no vararg for WorkerFactory
) {
. . .
            getKoin().getAll<WorkerFactory>()
                .forEach {
                    delegatingWorkerFactory.addFactory(it)
                }
}
```