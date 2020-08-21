package org.koin.sample.androidx.mvvm

import android.os.Bundle
import android.view.View
import org.junit.Assert.*
import org.koin.android.ext.android.getKoin
import org.koin.androidx.scope.ScopeFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.emptyState
import org.koin.core.parameter.parametersOf
import org.koin.sample.android.R
import org.koin.sample.androidx.components.ID
import org.koin.sample.androidx.components.mvvm.SavedStateViewModel
import org.koin.sample.androidx.components.mvvm.SimpleViewModel
import org.koin.sample.androidx.components.scope.Session

class MVVMFragment(val session: Session) : ScopeFragment(contentLayoutId = R.layout.mvvm_fragment) {

    val simpleViewModel: SimpleViewModel by viewModel { parametersOf(ID) }

    val shared: SimpleViewModel by sharedViewModel { parametersOf(ID) }
    val sharedSaved: SavedStateViewModel by sharedViewModel { parametersOf(ID) }

    val saved by viewModel<SavedStateViewModel>(state = emptyState()) { parametersOf(ID) }
    val saved2 by viewModel<SavedStateViewModel>(state = emptyState()) { parametersOf(ID) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assertNotNull(session)

        assertNotEquals(shared, simpleViewModel)

        assertEquals((requireActivity() as MVVMActivity).simpleViewModel, shared)
        assertEquals((requireActivity() as MVVMActivity).savedVm, sharedSaved)

        assertNotEquals((requireActivity() as MVVMActivity).savedVm, saved)
        assertNotEquals((requireActivity() as MVVMActivity).savedVm, saved2)

        assertEquals(saved, saved2)

        assertEquals(scopeActivity?.get<Session>()?.id, getKoin().getProperty("session_id"))
    }
}
