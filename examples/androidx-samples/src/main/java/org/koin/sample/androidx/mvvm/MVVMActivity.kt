package org.koin.sample.androidx.mvvm

import android.os.Bundle
import kotlinx.android.synthetic.main.mvvm_activity.*
import org.koin.android.ext.android.getKoin
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.androidx.components.ID
import org.koin.sample.androidx.components.mvvm.ExtSimpleViewModel
import org.koin.sample.androidx.components.mvvm.SavedStateBundleViewModel
import org.koin.sample.androidx.components.mvvm.SavedStateViewModel
import org.koin.sample.androidx.components.mvvm.SimpleViewModel
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.scope.ScopedActivityA
import org.koin.sample.androidx.utils.navigateTo

class MVVMActivity : ScopeActivity(contentLayoutId = R.layout.mvvm_activity) {

    val simpleViewModel: SimpleViewModel by viewModel { parametersOf(ID) }

    val vm1: SimpleViewModel by viewModel(named("vm1")) { parametersOf("vm1") }
    val vm2: SimpleViewModel by viewModel(named("vm2")) { parametersOf("vm2") }

    val scopeVm: ExtSimpleViewModel by viewModel()
    val extScopeVm: ExtSimpleViewModel by viewModel(named("ext"))

    //    val savedVm: SavedStateViewModel by stateViewModel { parametersOf("vm1") }
    val savedVm: SavedStateViewModel by viewModel { parametersOf("vm1") }

    //    val state = Bundle().apply { putString("id", "vm1") }
//    val stateVM: SavedStateBundleViewModel by stateViewModel(state = { state })
    val stateVM: SavedStateBundleViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        // should set `lifecycleScope` here because we're
        // using MVVMActivity with scope in mvvmModule (AppModule)
        setupKoinFragmentFactory(scope)
        super.onCreate(savedInstanceState)

        title = "Android MVVM"

        supportFragmentManager.beginTransaction()
            .replace<MVVMFragment>(R.id.mvvm_frame)
            .commit()

        getKoin().setProperty("session_id", scope.get<Session>().id)

        mvvm_button.setOnClickListener {
            navigateTo<ScopedActivityA>(isRoot = true)
        }

        checks()
    }

    private fun checks() {
        assert(scopeVm.session.id == extScopeVm.session.id)
        assert(stateVM.result == "vm1")
        assert(vm1.id != vm2.id)
    }
}