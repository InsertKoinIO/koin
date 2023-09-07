package org.koin.sample.sandbox.mvvm

import android.app.Application
import android.os.Bundle
import android.widget.Button
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.sample.sandbox.R
import org.koin.sample.sandbox.components.ID
import org.koin.sample.sandbox.components.mvp.FactoryPresenter
import org.koin.sample.sandbox.components.mvvm.*
import org.koin.sample.sandbox.components.scope.Session
import org.koin.sample.sandbox.scope.ScopedActivityA
import org.koin.sample.sandbox.utils.navigateTo

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

    val abstractVM : AbstractViewModel by viewModel()

    val presenter : FactoryPresenter by inject { parametersOf("_MVVMActivity_id_") }

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

        findViewById<Button>(R.id.mvvm_button).setOnClickListener {
            navigateTo<ScopedActivityA>(isRoot = true)
        }

        checks()
    }

    override fun onCloseScope() {
        println("closing scope & displaying presenter: ${presenter.id}")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("$this destroyed - scope closed? ${scope.closed}")
        assert(scope.closed)
    }

    private fun checks() {
        assert(abstractVM is ViewModelImpl)
        assert(scopeVm.session.id == extScopeVm.session.id)
        assert(stateVM.result == "vm1")
        assert(vm1.id != vm2.id)

        val p1 = scope.get<MVVMPresenter1>()
        val p2 = scope.get<MVVMPresenter2>()

        assert(p1.ctx == this)
        assert(p2.ctx == getKoin().get<Application>())
    }
}