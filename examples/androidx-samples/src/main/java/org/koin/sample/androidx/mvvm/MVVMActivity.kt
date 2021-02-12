package org.koin.sample.androidx.mvvm

import android.os.Bundle
import kotlinx.android.synthetic.main.mvvm_activity.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.androidx.components.ID
import org.koin.sample.androidx.components.mvvm.ExtSimpleViewModel
import org.koin.sample.androidx.components.mvvm.SavedStateViewModel
import org.koin.sample.androidx.components.mvvm.SimpleViewModel
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.scope.ScopedActivityA
import org.koin.sample.androidx.utils.navigateTo

class MVVMActivity : ScopeActivity(contentLayoutId = R.layout.mvvm_activity) {

    val simpleViewModel: SimpleViewModel by viewModel(clazz = SimpleViewModel::class) { parametersOf(ID) }

    val vm1: SimpleViewModel by viewModel(named("vm1")) { parametersOf("vm1") }
    val vm2: SimpleViewModel by viewModel(named("vm2")) { parametersOf("vm2") }

    val scopeVm: ExtSimpleViewModel by viewModel()
    val extScopeVm: ExtSimpleViewModel by viewModel(qualifier = named("ext"))

    val bundle = Bundle().apply { putString("vm1", "value to stateViewModel") }
    val savedVm: SavedStateViewModel by stateViewModel(state = { bundle }) { parametersOf("vm1") }

    val bundleStateScope = Bundle().apply { putString("vm2", "value to scope.stateViewModel") }
    val scopedSavedVm: SavedStateViewModel by stateViewModel(qualifier = named("vm3"), state = { bundleStateScope }) { parametersOf("vm3") }

    override fun onCreate(savedInstanceState: Bundle?) {
        // should set `lifecycleScope` here because we're
        // using MVVMActivity with scope in mvvmModule (AppModule)
        setupKoinFragmentFactory(scope)

        super.onCreate(savedInstanceState)

        assertNotNull(scope.get<Session>())

        assertEquals(getViewModel<SimpleViewModel> { parametersOf(ID) }, simpleViewModel)

        assertNotEquals(vm1, vm2)

        title = "Android MVVM"

        assertNotNull(scopeVm)
        assertNotNull(extScopeVm)
        assertEquals(scopeVm.session.id, extScopeVm.session.id)

        assertNotNull(savedVm)
        assertNotNull(scopedSavedVm)
        assertNotEquals(savedVm.id, scopedSavedVm.id)

        assertEquals("value to stateViewModel", savedVm.handle.get("vm1"))
        assertEquals("value to scope.stateViewModel", scopedSavedVm.handle.get("vm2"))

        supportFragmentManager.beginTransaction()
                .replace<MVVMFragment>(R.id.mvvm_frame)
                .commit()

        getKoin().setProperty("session_id", scope.get<Session>().id)

        mvvm_button.setOnClickListener {
            navigateTo<ScopedActivityA>(isRoot = true)
        }
    }
}