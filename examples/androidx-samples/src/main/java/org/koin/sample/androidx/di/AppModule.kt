package org.koin.sample.androidx.di

import org.koin.sample.androidx.navigation.NavViewModel
import org.koin.androidx.fragment.dsl.fragmentOf
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.*
import org.koin.core.qualifier.named
import org.koin.dsl.*
import org.koin.sample.androidx.components.Counter
import org.koin.sample.androidx.components.SCOPE_ID
import org.koin.sample.androidx.components.SCOPE_SESSION
import org.koin.sample.androidx.components.main.*
import org.koin.sample.androidx.components.mvp.FactoryPresenter
import org.koin.sample.androidx.components.mvp.ScopedPresenter
import org.koin.sample.androidx.components.mvvm.*
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.components.scope.SessionActivity
import org.koin.sample.androidx.mvp.MVPActivity
import org.koin.sample.androidx.mvvm.MVVMActivity
import org.koin.sample.androidx.mvvm.MVVMFragment
import org.koin.sample.androidx.scope.ScopedActivityA
import org.koin.sample.androidx.workmanager.SimpleWorker
import org.koin.sample.androidx.workmanager.SimpleWorkerService

val appModule = module {

    singleOf(::SimpleServiceImpl) { bind<SimpleService>() }
    singleOf(::DumbServiceImpl){
        named("dumb")
        bind<SimpleService>()
    }
    factory { RandomId() }
}

val mvpModule = module {
    //factory { (id: String) -> FactoryPresenter(id, get()) }
    factoryOf(::FactoryPresenter)

    scope<MVPActivity> {
        scopedOf(::ScopedPresenter)// { (id: String) -> ScopedPresenter(id, get()) }
    }
}

val mvvmModule = module {

    viewModel { (id: String) -> SimpleViewModel(id, get()) }

    viewModelOf(::SimpleViewModel){ named("vm1") } //{ (id: String) -> SimpleViewModel(id, get()) }
    viewModel(named("vm2")) { (id: String) -> SimpleViewModel(id, get()) }

    viewModelOf(::SavedStateViewModel)// { params -> SavedStateViewModel(get(), params.get(), get()) }// injected params
    viewModelOf(::SavedStateBundleViewModel)// { SavedStateBundleViewModel(get(), get()) }// injected params

    scope<MVVMActivity> {

        scoped { Session() }
        fragmentOf(::MVVMFragment) // { MVVMFragment(get()) }
        viewModelOf(::ExtSimpleViewModel)
        viewModelOf(::ExtSimpleViewModel){ named("ext")}
        viewModelOf(::SavedStateViewModel){ named("vm2")}
    }
    scope<MVVMFragment> {
        scoped { (id: String) -> ScopedPresenter(id, get()) }
        scopedOf(::Session)
        viewModelOf(::ExtSimpleViewModel)
        viewModelOf(::ExtSimpleViewModel){ named("ext")}
    }
}

val scopeModule = module {
    scope(named(SCOPE_ID)) {
        scopedOf(::Session){
            named(SCOPE_SESSION)
            onClose {
                // onRelease, count it
                Counter.released++
                println("Scoped -SCOPE_SESSION- release = ${Counter.released}")
            }
        }
    }
}

val scopeModuleActivityA = module {
    scope<ScopedActivityA> {
        scopedOf(::Session)
        scopedOf(::SessionActivity)
    }
}

val workerServiceModule = module {
    singleOf(::SimpleWorkerService)
}

val workerScopedModule = module {
    workerOf(::SimpleWorker)// { SimpleWorker(get(), androidContext(), it.get()) }
}

val navModule = module {
    viewModelOf(::NavViewModel)
}

// workerScopedModule can't be runned in unit test
val allModules = appModule + mvpModule + mvvmModule + scopeModule + workerServiceModule + workerScopedModule + navModule

val allTestModules = appModule + mvpModule + mvvmModule + scopeModule + workerServiceModule + navModule + scopeModuleActivityA