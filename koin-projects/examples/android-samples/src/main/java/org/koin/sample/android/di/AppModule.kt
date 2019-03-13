package org.koin.sample.android.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.sample.android.components.main.DumbServiceImpl
import org.koin.sample.android.components.main.RandomId
import org.koin.sample.android.components.main.Service
import org.koin.sample.android.components.main.ServiceImpl
import org.koin.sample.android.components.mvp.FactoryPresenter
import org.koin.sample.android.components.mvp.ScopedPresenter
import org.koin.sample.android.components.mvvm.SimpleViewModel
import org.koin.sample.android.mvp.MVPActivity

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
}