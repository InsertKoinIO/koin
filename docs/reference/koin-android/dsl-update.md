---
title: Constructor DSL for Android
---

## New Constructor DSL (Since 3.2)

Koin now offer a new kind of DSL keyword that allow you to target a class constructor directly, and avoid to have type your definition within a lambda expression.

Check the new [Constructor DSL](../koin-core/dsl-update.md#constructor-dsl-since-32) section for more details.

For Android, this implies the following new constructor DSL Keyword:

* `viewModelOf()` - equivalent of `viewModel { }`
* `fragmentOf()` - equivalent of `fragment { }`
* `workerOf()` - equivalent of `worker { }`

:::info
Be sure to use `::` before your class name, to target your class constructor
:::

### An Android example

Given an Android application with the following components:

```kotlin
// A simple service
class SimpleServiceImpl() : SimpleService

// a Presenter, using SimpleService and can receive "id" injected param
class FactoryPresenter(val id: String, val service: SimpleService)

// a ViewModel that can receive "id" injected param, use SimpleService and get SavedStateHandle
class SimpleViewModel(val id: String, val service: SimpleService, val handle: SavedStateHandle) : ViewModel()

// a scoped Session, that can received link to the MyActivity (from scope)
class Session(val activity: MyActivity)

// a Worker, using SimpleService and getting Context & WorkerParameters
class SimpleWorker(
    private val simpleService: SimpleService,
    appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params)
```

we can declare them like this:

```kotlin
module {
    singleOf(::SimpleServiceImpl){ bind<SimpleService>() }

    factoryOf(::FactoryPresenter)

    viewModelOf(::SimpleViewModel)

    scope<MyActivity>(){
        scopedOf(::Session) 
    }

    workerOf(::SimpleWorker)
}
```

## Android Reflection DSL (Deprecated since 3.2)

:::caution
Koin Reflection DSL is now deprecated. Please Use Koin Constructor DSL above
:::
