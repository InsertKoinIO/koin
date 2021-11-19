---
title: Android DSL - 3.1.x Update
---

## Compact Definition

Koin DSL can be seen as "manual", while you must fill constructors with "get()" function to resolve needed instances. When your definition don't need any special constructor integration (injection parameters or special scope Id), we can go with more compact writing style thanks to API below.

All injected parameters are also resolved directly with this compact form of writing.

You can freely write `viewModel()`, `fragment()` or even `worker<>()`. All parameters will be passed to your constructor.

given the following classes:

```kotlin
class SimpleServiceImpl(override val id: String = SERVICE_IMPL) : SimpleService

class FactoryPresenter(val id: String, val service: SimpleService)
class ScopedPresenter(val id: String, val service: SimpleService)

class SimpleViewModel(val id: String, val service: SimpleService) : ViewModel()
class SavedStateViewModel(val handle: SavedStateHandle, val id: String, val service: SimpleService) : ViewModel()

class MVVMFragment(val session: Session) : ScopeFragment(contentLayoutId = R.layout.mvvm_fragment)

class SimpleWorker(
    private val simpleWorkerService: SimpleWorkerService,
    appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params)
```

Here are the module declaration:

```kotlin
val appModule = module {
    // Declare a singleton SimpleServiceImpl, binding SimpleService
    single<SimpleServiceImpl>() bind SimpleService::class
}

val mvpModule = module {
    // Simple Presenter with injected parameters
    factory { (id: String) -> FactoryPresenter(id, get()) }
    // also declared like this
    factory<FactoryPresenter>()

    scope<MVPActivity> {
        // presenter scoped to MVPActivity
        scoped { (id: String) -> ScopedPresenter(id, get()) }
        // also declared like this
        scoped<ScopedPresenter>()
    }
}

val mvvmModule = module {

    // ViewModel with parameter injection
    viewModel { (id: String) -> SimpleViewModel(id, get()) }
    // also declared like this
    viewModel<SimpleViewModel>()

    // Get SaveStateHandle from parameters
    viewModel { params -> SavedStateViewModel(params.get(), params.get(), get()) }
    // also declared like this
    viewModel<SavedStateViewModel>()

    scope<MVVMActivity> {
        scoped { Session() }
        // fragment scoped in MVVMActivity, will have access to Session
        fragment<MVVMFragment>()
    }
}

val workerScopedModule = module {
    single<SimpleWorkerService>()
    worker { params -> SimpleWorker(get(), get(), params.get()) }
}
```

:::note
 Using reflection is not costless, even if here it's really minimal. it replaces what you don"t want to write with reflection code (finding primary constructors, injecting parameters...). Mind it before using it, if you are on performances constraints platform (Android for example)
:::

:::info
 You must declare class constructors in your proguard file
:::