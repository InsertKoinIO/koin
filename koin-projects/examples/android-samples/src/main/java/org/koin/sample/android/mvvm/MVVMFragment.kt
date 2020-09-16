package org.koin.sample.android.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.junit.Assert.*
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.ScopeFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.android.components.ID
import org.koin.sample.android.components.mvvm.ExtSimpleViewModel
import org.koin.sample.android.components.mvvm.SimpleViewModel
import org.koin.sample.android.components.scope.Session

class MVVMFragment : ScopeFragment() {

    val sharedViewModel: SimpleViewModel by sharedViewModel { parametersOf(ID) }
    val simpleViewModel: SimpleViewModel by viewModel { parametersOf(ID) }
    val session: Session? by lazy { scopeActivity?.scope?.get<Session>() }

    val scopeVm: ExtSimpleViewModel by viewModel()
    val extScopeVm: ExtSimpleViewModel by viewModel(named("ext"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mvvm_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assertNotEquals(sharedViewModel, simpleViewModel)
        assertEquals((activity as MVVMActivity).simpleViewModel, sharedViewModel)

        assertEquals(session?.id, getKoin().getProperty("session_id"))

        assertNotEquals(simpleViewModel, (activity as MVVMActivity).simpleViewModel)
        assertEquals(sharedViewModel, (activity as MVVMActivity).simpleViewModel)

        assertNotNull(scopeVm)
        assertNotNull(extScopeVm)
        assertEquals(scopeVm.session.id, extScopeVm.session.id)
    }
}