package org.koin.sample.androidx.di

import org.koin.androidx.experimental.dsl.autoViewModel
import androidx.lifecycle.SavedStateHandle
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.dsl.onRelease
import org.koin.sample.androidx.components.Counter
import org.koin.sample.androidx.components.SCOPE_ID
import org.koin.sample.androidx.components.SCOPE_SESSION
import org.koin.sample.androidx.components.main.DumbServiceImpl
import org.koin.sample.androidx.components.main.RandomId
import org.koin.sample.androidx.components.main.Service
import org.koin.sample.androidx.components.main.ServiceImpl
import org.koin.sample.androidx.components.mvp.FactoryPresenter
import org.koin.sample.androidx.components.mvp.ScopedPresenter
import org.koin.sample.androidx.components.mvvm.ExtSimpleViewModel
import org.koin.sample.androidx.components.mvvm.SavedStateViewModel
import org.koin.sample.androidx.components.mvvm.SimpleViewModel
import org.koin.sample.androidx.components.scope.Controller
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.mvp.MVPActivity
import org.koin.sample.androidx.mvvm.MVVMActivity
import org.koin.sample.androidx.mvvm.MVVMFragment
import org.koin.sample.androidx.scope.ScopedActivityA

val appModule = module {

    single<Service> { ServiceImpl() }
    single<Service>(named("dumb")) { DumbServiceImpl() }

    factory { RandomId() }
}

val mvpModule = module {
    factory { (id: String) -> FactoryPresenter(id, get()) }

    objectScope<MVPActivity> {
        scoped { (id: String) ->
            ScopedPresenter(id, get())
        }
    }
}

val mvvmModule = module {
    viewModel { (id: String) -> SimpleViewModel(id, get()) }

    viewModel(named("vm1")) { (id: String) -> SimpleViewModel(id, get()) }
    viewModel(named("vm2")) { (id: String) -> SimpleViewModel(id, get()) }

    viewModel(useState = true) { (handle: SavedStateHandle, id: String) ->
        SavedStateViewModel(handle, id, get())
    }

    objectScope<MVVMActivity>() {

        scoped { Session() }
        scoped { Controller(get<MVVMActivity>()) }
        viewModel { ExtSimpleViewModel(get()) }

        autoViewModel<ExtSimpleViewModel>(named("ext"))
        viewModel(useState = true) { (handle: SavedStateHandle, id: String) ->
            SavedStateViewModel(handle, id, get())
        }

        childObjectScope<MVVMFragment>()
    }
}

val scopeModule = module {
    scope(named(SCOPE_ID)) {
        scoped(named(SCOPE_SESSION)) { Session() } onRelease {
            // onRelease, count it
            Counter.released++
            println("Scoped -SCOPE_SESSION- release = ${Counter.released}")
        }
    }
    objectScope<ScopedActivityA> {
        scoped { Session() }
    }
}