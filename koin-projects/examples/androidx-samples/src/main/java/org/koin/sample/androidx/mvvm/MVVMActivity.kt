package org.koin.sample.androidx.mvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.mvvm_activity.*
import org.junit.Assert.*
import org.koin.android.ext.android.getKoin
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.koinScope
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
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

    val scopeVm: ExtSimpleViewModel by koinScope.viewModel(this)
    val extScopeVm: ExtSimpleViewModel by koinScope.viewModel(this, named("ext"))

    val savedVm: SavedStateViewModel by viewModel { parametersOf(Bundle(), "vm1") }
    val scopedSavedVm: SavedStateViewModel by koinScope.viewModel(this, named("vm2")) {
        parametersOf(
                Bundle(),
                "vm2"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // if not in scope
        //setupKoinFragmentFactory()
        setupKoinFragmentFactory(koinScope)

        super.onCreate(savedInstanceState)

        assertNotNull(koinScope.get<Session>())

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

        supportFragmentManager.beginTransaction()
                .replace(R.id.mvvm_frame, MVVMFragment::class.java, null, null)
                .commit()

        getKoin().setProperty("session", koinScope.get<Session>())

        mvvm_button.setOnClickListener {
            navigateTo<ScopedActivityA>(isRoot = true)
        }

    }
}