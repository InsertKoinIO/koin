package org.koin.sample.sandbox.di

// 4.0 deprecations
//import org.koin.androidx.viewmodel.dsl.viewModel
//import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.fragment.dsl.fragmentOf
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.androidx.scope.dsl.activityScope
import org.koin.androidx.scope.dsl.fragmentScope
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.moduleConfiguration
import org.koin.core.qualifier.named
import org.koin.dsl.lazyModule
import org.koin.dsl.module
import org.koin.sample.sandbox.components.Counter
import org.koin.sample.sandbox.components.SCOPE_ID
import org.koin.sample.sandbox.components.SCOPE_SESSION
import org.koin.sample.sandbox.components.main.DumbServiceImpl
import org.koin.sample.sandbox.components.main.RandomId
import org.koin.sample.sandbox.components.main.SimpleService
import org.koin.sample.sandbox.components.main.SimpleServiceImpl
import org.koin.sample.sandbox.components.mvp.FactoryPresenter
import org.koin.sample.sandbox.components.mvp.ScopedPresenter
import org.koin.sample.sandbox.components.mvvm.AbstractViewModel
import org.koin.sample.sandbox.components.mvvm.MVVMPresenter1
import org.koin.sample.sandbox.components.mvvm.MVVMPresenter2
import org.koin.sample.sandbox.components.mvvm.MyScopeViewModel
import org.koin.sample.sandbox.components.mvvm.MyScopeViewModel2
import org.koin.sample.sandbox.components.mvvm.SavedStateBundleViewModel
import org.koin.sample.sandbox.components.mvvm.SavedStateViewModel
import org.koin.sample.sandbox.components.mvvm.SharedVM
import org.koin.sample.sandbox.components.mvvm.SimpleViewModel
import org.koin.sample.sandbox.components.mvvm.ViewModelImpl
import org.koin.sample.sandbox.components.scope.Session
import org.koin.sample.sandbox.components.scope.SessionActivity
import org.koin.sample.sandbox.components.scope.SessionConsumer
import org.koin.sample.sandbox.mvvm.MVVMFragment
import org.koin.sample.sandbox.navigation.NavViewModel
import org.koin.sample.sandbox.navigation.NavViewModel2
import org.koin.sample.sandbox.scope.ScopedFragment
import org.koin.sample.sandbox.workmanager.SimpleWorker
import org.koin.sample.sandbox.workmanager.SimpleWorkerService
import org.koin.viewmodel.scope.viewModelScope

val appModule = module {

    singleOf(::SimpleServiceImpl) { bind<SimpleService>() }
    singleOf(::DumbServiceImpl) {
        named("dumb")
        bind<SimpleService>()
    }
    factory { RandomId() }
}

val mvpModule = lazyModule {
    //factory { (id: String) -> FactoryPresenter(id, get()) }
    factoryOf(::FactoryPresenter)

    activityScope {
        scopedOf(::ScopedPresenter)
    }
//    scope<MVPActivity> {
//        scopedOf(::ScopedPresenter)// { (id: String) -> ScopedPresenter(id, get()) }
//
//    }
}

val mvvmModule = lazyModule {

    viewModelOf(::SimpleViewModel)// { (id: String) -> SimpleViewModel(id, get()) }
    viewModelOf(::SimpleViewModel) { named("vm1") } //{ (id: String) -> SimpleViewModel(id, get()) }
    viewModel(named("vm2")) { (id: String) -> SimpleViewModel(id, get()) }

    viewModelOf(::SavedStateViewModel)// { params -> SavedStateViewModel(get(), params.get(), get()) }// injected params
    viewModelOf(::SavedStateBundleViewModel)// { SavedStateBundleViewModel(get(), get()) }// injected params

    // viewModel<AbstractViewModel> { ViewModelImpl(get()) }
    viewModelOf(::ViewModelImpl) { bind<AbstractViewModel>() }

    viewModelOf(::MyScopeViewModel)
    viewModelOf(::MyScopeViewModel2)

    viewModelScope {
        scopedOf(::Session)
        scopedOf(::SessionConsumer)
    }

//    scope<MyScopeViewModel> {
//        scopedOf(::Session)
//    }

//
//    scope<MyScopeViewModel2> {
//        scopedOf(::Session)
//        scopedOf(::SessionConsumer)
//    }

    viewModelOf(::SavedStateViewModel) { named("vm2") }

    viewModel { (s: Session) -> SharedVM(s) }

    activityScope {
        scopedOf(::Session)
        fragmentOf(::MVVMFragment) // { MVVMFragment(get()) }

        scoped { MVVMPresenter1(get()) }
        scoped { MVVMPresenter2(get()) }
    }
    fragmentScope {
        scoped { (id: String) -> ScopedPresenter(id, get()) }
    }

//    scope<MVVMActivity> {
//        scopedOf(::Session)
//        fragmentOf(::MVVMFragment) // { MVVMFragment(get()) }
//
//        scoped { MVVMPresenter1(get()) }
//        scoped { MVVMPresenter2(get()) }
//    }
//    scope<MVVMFragment> {
//        scoped { (id: String) -> ScopedPresenter(id, get()) }
//        // to retrieve from parent
////        scopedOf(::Session)
//    }
}

val scopeModule = lazyModule {
    scope(named(SCOPE_ID)) {
        scopedOf(::Session) {
            named(SCOPE_SESSION)
            onClose {
                // onRelease, count it
                Counter.released++
                println("Scoped -SCOPE_SESSION- release = ${Counter.released}")
            }
        }
    }
}

val scopeModuleActivityA = lazyModule {
    activityRetainedScope {
        fragmentOf(::ScopedFragment)
        scopedOf(::Session)
        scopedOf(::SessionActivity)
    }
//    scope<ScopedActivityA> {
//        fragmentOf(::ScopedFragment)
//        scopedOf(::Session)
//        scopedOf(::SessionActivity)
//    }
//    scope<ScopedFragment> {
//    }
}

val workerServiceModule = lazyModule {
    singleOf(::SimpleWorkerService)
}

val workerScopedModule = lazyModule {
    workerOf(::SimpleWorker)// { SimpleWorker(get(), androidContext(), it.get()) }
}

val navModule = lazyModule {
    viewModelOf(::NavViewModel)
    viewModelOf(::NavViewModel2)
}

val appModules = moduleConfiguration {
    modules(appModule)
    lazyModules(mvpModule,
        mvvmModule,
        scopeModule,
        workerServiceModule,
        workerScopedModule,
        navModule,
        scopeModuleActivityA)
}