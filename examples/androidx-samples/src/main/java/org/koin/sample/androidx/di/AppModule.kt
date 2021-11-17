package org.koin.sample.androidx.di

import org.koin.android.ext.koin.androidContext
import org.koin.sample.androidx.navigation.NavViewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
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

    single<SimpleServiceImpl>() bind SimpleService::class
    single<DumbServiceImpl>(named("dumb")) bind SimpleService::class
    factory { p -> SimplePresenter(p.get())}
    factory { RandomId() }
}

val mvpModule = module {
    factory { (id: String) -> FactoryPresenter(id, get()) }

    scope<MVPActivity> {
        scoped { (id: String) -> ScopedPresenter(id, get()) }
    }
}

val mvvmModule = module {

    viewModel { (id: String) -> SimpleViewModel(id, get()) }

    viewModel(named("vm1")) { (id: String) -> SimpleViewModel(id, get()) }
    viewModel(named("vm2")) { (id: String) -> SimpleViewModel(id, get()) }

    viewModel { params -> SavedStateViewModel(get(), params.get(), get()) }// injected params

    scope<MVVMActivity> {

        viewModel { OneViewModel() }
        scoped { UseOneViewModel(get())}

        scoped { Session() }
        fragment<MVVMFragment>()
        viewModel<ExtSimpleViewModel>()
        viewModel<ExtSimpleViewModel>(named("ext"))
        viewModel<SavedStateViewModel>(named("vm2")) // graph injected usage + builder API
    }
    scope<MVVMFragment> {
        scoped { (id: String) -> ScopedPresenter(id, get()) }
        scoped<Session>()
        viewModel<ExtSimpleViewModel>()
        viewModel(named("ext")) { ExtSimpleViewModel(get()) }
    }
}

val scopeModule = module {
    scope(named(SCOPE_ID)) {
        scoped(named(SCOPE_SESSION)) { Session() } onClose {
            // onRelease, count it
            Counter.released++
            println("Scoped -SCOPE_SESSION- release = ${Counter.released}")
        }
    }

}

val scopeModuleActivityA = module {
    scope<ScopedActivityA> {
        scoped<Session>()
        scoped { SessionActivity(get()) }
    }
}

val workerServiceModule = module {
    single<SimpleWorkerService>()
}

val workerScopedModule = module {
    worker { SimpleWorker(get(), androidContext(), it.get()) }
}

val navModule = module {
    viewModel<NavViewModel>()
}

// workerScopedModule can't be runned in unit test
val allModules = appModule + mvpModule + mvvmModule + scopeModule + workerServiceModule + workerScopedModule + navModule

val allTestModules = appModule + mvpModule + mvvmModule + scopeModule + workerServiceModule + navModule + scopeModuleActivityA