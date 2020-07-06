package org.koin.sample.androidx.di

import androidx.lifecycle.SavedStateHandle
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.koin.sample.androidx.components.Counter
import org.koin.sample.androidx.components.SCOPE_ID
import org.koin.sample.androidx.components.SCOPE_SESSION
import org.koin.sample.androidx.components.main.DumbServiceImpl
import org.koin.sample.androidx.components.main.RandomId
import org.koin.sample.androidx.components.main.SimpleService
import org.koin.sample.androidx.components.main.SimpleServiceImpl
import org.koin.sample.androidx.components.mvp.FactoryPresenter
import org.koin.sample.androidx.components.mvp.ScopedPresenter
import org.koin.sample.androidx.components.mvvm.ExtSimpleViewModel
import org.koin.sample.androidx.components.mvvm.SavedStateViewModel
import org.koin.sample.androidx.components.mvvm.SimpleViewModel
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.components.scope.SessionActivity
import org.koin.sample.androidx.mvp.MVPActivity
import org.koin.sample.androidx.mvvm.MVVMActivity
import org.koin.sample.androidx.mvvm.MVVMFragment
import org.koin.sample.androidx.scope.ScopedActivityA

val appModule = module {

    single<SimpleService> { SimpleServiceImpl() }
    single<SimpleService>(named("dumb")) { DumbServiceImpl() }

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

    viewModel { (handle: SavedStateHandle, id: String) -> SavedStateViewModel(handle, id, get()) }

    scope<MVVMActivity> {
        scoped { Session() }
        fragment { MVVMFragment(get()) }
        viewModel { ExtSimpleViewModel(get()) }
        viewModel<ExtSimpleViewModel>(named("ext"))
        viewModel(named("vm3")) { (handle: SavedStateHandle, id: String) ->
            SavedStateViewModel(
                handle,
                id,
                get()
            )
        }
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

    scope<ScopedActivityA> {
        scoped { Session() }
        scoped { SessionActivity(get()) }
    }
}

val allModules = appModule + mvpModule + mvvmModule + scopeModule