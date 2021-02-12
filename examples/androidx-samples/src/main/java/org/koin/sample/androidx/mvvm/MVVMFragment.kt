package org.koin.sample.androidx.mvvm

import android.os.Bundle
import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.koin.android.ext.android.get
import org.koin.androidx.scope.ScopeFragment
import org.koin.androidx.scope.requireScopeActivity
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.emptyState
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.androidx.components.ID
import org.koin.sample.androidx.components.mvvm.ExtSimpleViewModel
import org.koin.sample.androidx.components.mvvm.SavedStateViewModel
import org.koin.sample.androidx.components.mvvm.SimpleViewModel
import org.koin.sample.androidx.components.scope.Session

class MVVMFragment(val session: Session) : ScopeFragment(contentLayoutId = R.layout.mvvm_fragment) {

    val simpleViewModel: SimpleViewModel by viewModel { parametersOf(ID) }

    val scopeVm: ExtSimpleViewModel by viewModel()
    val extScopeVm: ExtSimpleViewModel by viewModel(named("ext"))

    val shared: SimpleViewModel by sharedViewModel { parametersOf(ID) }
    val sharedSaved: SavedStateViewModel by sharedViewModel { parametersOf(ID) }

    val saved by stateViewModel<SavedStateViewModel>(state = emptyState()) { parametersOf(ID) }
    val saved2 by stateViewModel<SavedStateViewModel>(state = emptyState()) { parametersOf(ID) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assertNotNull(session)

        assertNotEquals(shared, simpleViewModel)

        assertNotNull(scopeVm)
        assertNotNull(extScopeVm)
        assertEquals(scopeVm.session.id, extScopeVm.session.id)

        assertEquals((requireActivity() as MVVMActivity).simpleViewModel, shared)
        assertEquals((requireActivity() as MVVMActivity).savedVm, sharedSaved)

        assertNotEquals((requireActivity() as MVVMActivity).savedVm, saved)
        assertNotEquals((requireActivity() as MVVMActivity).savedVm, saved2)
        val shared2 = getSharedViewModel<SimpleViewModel> { parametersOf(ID) }
        val shared3 = getSharedViewModel(clazz = SimpleViewModel::class) { parametersOf(ID) }
        assertEquals(shared, shared2)
        assertEquals(shared2, shared3)

        assertEquals(saved, saved2)

        assertEquals(requireScopeActivity<MVVMActivity>().get<Session>().id, getKoin().getProperty("session_id"))
    }
}
