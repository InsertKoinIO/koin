package org.koin.sample.androidx.mvvm

import android.os.Bundle
import kotlinx.android.synthetic.main.mvvm_activity.*
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.androidx.components.ID
import org.koin.sample.androidx.components.mvvm.*
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.scope.ScopedActivityA
import org.koin.sample.androidx.utils.navigateTo

class MVVMActivity : ScopeActivity(contentLayoutId = R.layout.mvvm_activity) {

    val simpleViewModel: SimpleViewModel by viewModel() { parametersOf(ID) }

    val vm1: SimpleViewModel by viewModel(named("vm1")) { parametersOf("vm1") }
    val vm2: SimpleViewModel by viewModel(named("vm2")) { parametersOf("vm2") }

    val scopeVm: ExtSimpleViewModel by viewModel()
    val extScopeVm: ExtSimpleViewModel by viewModel(named("ext"))

    val savedVm: SavedStateViewModel by viewModel { parametersOf("vm1") }
//    val scopedSavedVm: SavedStateViewModel by viewModel(named("vm2")){ parametersOf("vm2") }

    // scope recreate
    val oneViewModel: OneViewModel by viewModel()
    val useOneViewModel: UseOneViewModel by inject()

    val state = Bundle().apply { putString("id","vm1") }
    val stateVM: SavedStateBundleViewModel by stateViewModel(state = {state})

    override fun onCreate(savedInstanceState: Bundle?) {
        // should set `lifecycleScope` here because we're
        // using MVVMActivity with scope in mvvmModule (AppModule)
        setupKoinFragmentFactory(scope)

        super.onCreate(savedInstanceState)

        scope.get<Session>()

        assert(getViewModel<SimpleViewModel> { parametersOf(ID) } == simpleViewModel)

        assert(vm1 != vm2)

        title = "Android MVVM"

        assert(scopeVm.session.id == extScopeVm.session.id)

//        assert(savedVm.id != scopedSavedVm.id)
////        assert("value to stateViewModel" == savedVm.handle.get("vm1"))
//        assert("value to scope.stateViewModel" == scopedSavedVm.handle.get("vm3"))

        supportFragmentManager.beginTransaction()
            .replace<MVVMFragment>(R.id.mvvm_frame)
            .commit()

        getKoin().setProperty("session_id", scope.get<Session>().id)

        mvvm_button.setOnClickListener {
            navigateTo<ScopedActivityA>(isRoot = true)
        }
        println("oneViewModel => $oneViewModel")
        println("useOneViewModel => ${useOneViewModel.vm}")

        assert(oneViewModel == useOneViewModel.vm)

        assert(stateVM.result == "vm1")
    }
}