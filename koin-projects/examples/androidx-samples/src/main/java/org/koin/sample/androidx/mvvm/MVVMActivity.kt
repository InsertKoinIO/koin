package org.koin.sample.androidx.mvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.mvvm_activity.*
import org.junit.Assert.*
import org.koin.android.ext.android.getKoin
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.stateViewModel
import org.koin.androidx.viewmodel.scope.viewModel
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

class MVVMActivity : AppCompatActivity() {

    val simpleViewModel: SimpleViewModel by viewModel(clazz = SimpleViewModel::class) {
        parametersOf(
            ID
        )
    }

    val vm1: SimpleViewModel by viewModel(named("vm1")) { parametersOf("vm1") }
    val vm2: SimpleViewModel by viewModel(named("vm2")) { parametersOf("vm2") }

    val scopeVm: ExtSimpleViewModel by lifecycleScope.viewModel(this)
    val extScopeVm: ExtSimpleViewModel by lifecycleScope.viewModel(this, named("ext"))

    val bundleStateVm = Bundle().apply { putString("vm1", "value to stateViewModel") }
    val savedVm: SavedStateViewModel by stateViewModel(bundle = { bundleStateVm }) { parametersOf("vm1") }

    val bundleStateScope = Bundle().apply {
        putString("vm2", "value to lifecycleScope.stateViewModel")
    }
    val scopedSavedVm: SavedStateViewModel by lifecycleScope.stateViewModel(this, named("vm2"), bundle = { bundleStateScope }) {
        parametersOf("vm2")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // should set `lifecycleScope` here because we're
        // using MVVMActivity with scope in mvvmModule (AppModule)
        setupKoinFragmentFactory(lifecycleScope)

        super.onCreate(savedInstanceState)

        assertNotNull(lifecycleScope.get<Session>())

        assertEquals(getViewModel<SimpleViewModel> { parametersOf(ID) }, simpleViewModel)

        assertNotEquals(vm1, vm2)

        title = "Android MVVM"
        setContentView(R.layout.mvvm_activity)

        assertNotNull(scopeVm)
        assertNotNull(extScopeVm)
        assertEquals(scopeVm.session.id, extScopeVm.session.id)

        assertNotNull(savedVm)
        assertNotNull(scopedSavedVm)
        assertNotEquals(savedVm.id, scopedSavedVm.id)

        assertEquals("value to stateViewModel", savedVm.handle.get("vm1"))
        assertEquals("value to lifecycleScope.stateViewModel", scopedSavedVm.handle.get("vm2"))

        supportFragmentManager.beginTransaction()
            .replace<MVVMFragment>(R.id.mvvm_frame)
            .commit()

        getKoin().setProperty("session_id", lifecycleScope.get<Session>().id)

        mvvm_button.setOnClickListener {
            navigateTo<ScopedActivityA>(isRoot = true)
        }

    }
}