package org.koin.sample.android.mvvm

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.sample.android.R
import org.koin.sample.android.components.ID
import org.koin.sample.android.components.mvvm.SimpleViewModel
import org.koin.sample.android.components.scope.Session

class MVVMFragment : Fragment() {

    val sharedViewModel: SimpleViewModel by sharedViewModel { parametersOf(ID) }
    val simpleViewModel: SimpleViewModel by viewModel { parametersOf(ID) }
    val session: Session? by lazy { activity?.currentScope?.get<Session>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mvvm_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assertNotEquals(sharedViewModel, simpleViewModel)
        assertEquals((activity as MVVMActivity).simpleViewModel, sharedViewModel)

        assertEquals(session, getKoin().getProperty("session"))
    }
}