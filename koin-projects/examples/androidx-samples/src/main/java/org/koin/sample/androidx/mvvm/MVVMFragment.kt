package org.koin.sample.androidx.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.sample.android.R
import org.koin.sample.androidx.components.ID
import org.koin.sample.androidx.components.mvvm.SimpleViewModel

class MVVMFragment : Fragment() {

    val shared: SimpleViewModel by sharedViewModel { parametersOf(ID) }
    val simpleViewModel: SimpleViewModel by viewModel { parametersOf(ID) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mvvm_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assertNotEquals(shared, simpleViewModel)
        assertEquals((activity as MVVMActivity).simpleViewModel, shared)
    }
}