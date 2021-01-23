package org.koin.sample

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.sample.view.java.MyJavaPresenter
import org.koin.sample.view.scope.MyScopeActivity
import org.koin.sample.view.scope.MyScopePresenter
import org.koin.sample.view.simple.MySimplePresenter
import org.koin.sample.view.viewmodel.MyViewModel

@JvmField
val appModule = module {
    // single instance of HelloRepository
    single<HelloRepository> { HelloRepositoryImpl() }

    // Simple Presenter Factory
    factory { MySimplePresenter(get()) }

    // Simple Java Presenter
    factory { MyJavaPresenter(get()) }

    // scope for MyScopeActivity
    scope<MyScopeActivity> {
        // scoped MyScopePresenter instance
        scoped { MyScopePresenter(get()) }
    }

    // MyViewModel ViewModel
    viewModel { MyViewModel(get()) }
}