package org.koin.sample.android.di

import org.koin.android.experimental.dsl.viewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.dsl.onRelease
import org.koin.sample.android.components.Counter
import org.koin.sample.android.components.SCOPE_ID
import org.koin.sample.android.components.SCOPE_SESSION
import org.koin.sample.android.components.dynamic.DynScoped
import org.koin.sample.android.components.dynamic.DynSingle
import org.koin.sample.android.components.main.DumbServiceImpl
import org.koin.sample.android.components.main.RandomId
import org.koin.sample.android.components.main.Service
import org.koin.sample.android.components.main.ServiceImpl
import org.koin.sample.android.components.mvp.FactoryPresenter
import org.koin.sample.android.components.mvp.ScopedPresenter
import org.koin.sample.android.components.mvvm.ExtSimpleViewModel
import org.koin.sample.android.components.mvvm.SimpleViewModel
import org.koin.sample.android.components.scope.Session
import org.koin.sample.android.mvp.MVPActivity
import org.koin.sample.android.mvvm.MVVMActivity
import org.koin.sample.android.scope.ScopedActivityA

val appModule = module {

    single<Service> { ServiceImpl() }
    single<Service>(named("dumb")) { DumbServiceImpl() }

    factory { RandomId() }
}

val mvpModule = module {
    factory { (id: String) -> FactoryPresenter(id, get()) }

    scope(named<MVPActivity>()) {
        scoped { (id: String) -> ScopedPresenter(id, get()) }
    }
}

val mvvmModule = module {
    viewModel { (id: String) -> SimpleViewModel(id, get()) }

    viewModel(named("vm1")) { (id: String) -> SimpleViewModel(id, get()) }
    viewModel(named("vm2")) { (id: String) -> SimpleViewModel(id, get()) }


    scope(named<MVVMActivity>()) {
        scoped { Session() }
        viewModel { ExtSimpleViewModel(get()) }
        viewModel<ExtSimpleViewModel>(named("ext"))
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
    scope(named<ScopedActivityA>()) {
        scoped { Session() }
    }
}

val dynamicModule = module {
    single { DynSingle() }
    scope(named("dynamic_scope")) {
        scoped { DynScoped() }
    }
}