package org.koin.sample.androidx.di

import androidx.lifecycle.SavedStateHandle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
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
import org.koin.sample.androidx.components.objectscope.SomeService
import org.koin.sample.androidx.components.objectscope.OtherService
import org.koin.sample.androidx.components.objectscope.Consumer
import org.koin.sample.androidx.components.objectscope.InterceptorA
import org.koin.sample.androidx.components.objectscope.InterceptorB
import org.koin.sample.androidx.components.objectscope.InterceptorC
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.mvp.MVPActivity
import org.koin.sample.androidx.mvvm.MVVMActivity
import org.koin.sample.androidx.mvvm.MVVMFragment
import org.koin.sample.androidx.scope.ObjectScopeActivity
import org.koin.sample.androidx.scope.ObjectScopeFragment
import org.koin.sample.androidx.scope.PassingScopeActivity
import org.koin.sample.androidx.scope.PassingScopeFragment
import org.koin.sample.androidx.scope.ScopedActivityA

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

    viewModel(useState = true) { (handle: SavedStateHandle, id: String) -> SavedStateViewModel(handle, id, get()) }

    scope(named<MVVMActivity>()) {

        scoped { Session() }
        viewModel { ExtSimpleViewModel(get()) }

        viewModel<ExtSimpleViewModel>(named("ext"))
        viewModel(useState = true) { (handle: SavedStateHandle, id: String) -> SavedStateViewModel(handle, id, get()) }
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

/**
 * All dependencies are known here, no need for parameters
 */
val objectScopeModule = module {
    objectScope<ObjectScopeActivity> {
        scoped<FragmentActivity> { get<ObjectScopeActivity>() }
        scoped { SomeService(get()) }
        scoped { OtherService(get()) }

        childObjectScope<ObjectScopeFragment> {
            scoped { InterceptorA(get()) }
            scoped { InterceptorB(get<ObjectScopeFragment>()) }
            scoped { InterceptorC(get()) }
            scoped {
                val interceptors = listOf(
                        get<InterceptorA>(),
                        get<InterceptorB>(),
                        get<InterceptorC>()
                )
                Consumer(interceptors, get())
            }
        }
    }
}

/**
 * This works, but it's cluttered with parameters. When a new dependency is added, it might have
 * to be passed through several chains of bean definitions. When the object graph changes, the parameter chain
 * might need to be adjusted and therefore all calls from the injection sites.
 * Here, all activity scoped dependencies have to be passed as parameters in order to work, along with
 * the fragment instance itself.
 */
val passingScopeModule = module {
    scope(named<PassingScopeActivity>()) {

        scoped { (activity: FragmentActivity) -> SomeService(activity) }
        scoped { (activity: FragmentActivity) -> OtherService(activity) }

        scope(named<PassingScopeFragment>()) {
            scoped { (activity: FragmentActivity) -> InterceptorA(activity) }
            scoped { (fragment: Fragment) -> InterceptorB(fragment) }
            scoped { (otherService: OtherService) -> InterceptorC(otherService) }
            scoped { (activity: FragmentActivity) -> SomeService(activity) }
            scoped { (fragment: PassingScopeFragment, someService: SomeService, otherService: OtherService) ->
                val parametersActivity = { parametersOf(fragment.activity!!) }
                val parametersFragment = { parametersOf(fragment) }
                val interceptors = listOf(
                        get<InterceptorA>(parameters = parametersActivity),
                        get<InterceptorB>(parameters = parametersFragment),
                        get<InterceptorC>(parameters = { parametersOf(otherService) })
                )
                Consumer(interceptors, someService)
            }
        }
    }
}