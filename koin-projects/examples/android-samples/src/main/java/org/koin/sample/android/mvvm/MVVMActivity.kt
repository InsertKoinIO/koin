package org.koin.sample.android.mvvm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.junit.Assert.assertEquals
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.sample.android.components.ID
import org.koin.sample.android.components.mvvm.SimpleViewModel

class MVVMActivity : AppCompatActivity() {

    val simpleViewModel: SimpleViewModel by viewModel { parametersOf(ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertEquals(getViewModel<SimpleViewModel> { parametersOf(ID) }, simpleViewModel)

        title = "Android MVVM"
    }
}