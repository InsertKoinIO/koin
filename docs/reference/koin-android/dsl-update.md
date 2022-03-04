---
title: Android Extended DSL (3.2)
---

## New Constructor DSL (Since 3.2)

Koin now offer a new kind of DSL keyword that allow you to target a class constructor directly, and avoid to to have type your definition within a lambda expression. 

Check the new [Constructor DSL](../koin-core/dsl-update.md#constructor-dsl-since-32) section for more details.

For Android, this implies the following new constructore DSL Keyword:

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

Koin DSL can be seen as "manual", while you must fill constructors with "get()" function to resolve needed instances. When your definition don't need any special constructor integration (injection paarameters or special scope Id), we can go with more compact writing style thanks to API below.

All injected parameters are also resolved directly with this compact form of writing.

You can freely write `viewModel()`, `fragment()` or even `worker<>()`. All parameters will be passed to your constructor.

:::note
 Using reflection is not costless, even if here it's really minimal. it replaces what you don"t want to write with reflection code (finding primary constructors, injecting parameters...). Mind it before using it, if you are on performances constraints platform (Android for example)
:::
