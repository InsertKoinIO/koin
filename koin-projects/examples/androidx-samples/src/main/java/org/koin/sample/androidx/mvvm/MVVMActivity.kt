package org.koin.sample.androidx.mvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.mvvm_activity.*
import org.junit.Assert.assertEquals
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.sample.android.R
import org.koin.sample.androidx.components.ID
import org.koin.sample.androidx.components.mvvm.SimpleViewModel
import org.koin.sample.androidx.scope.ScopedActivityA
import org.koin.sample.androidx.utils.navigateTo

class MVVMActivity : AppCompatActivity() {

    val simpleViewModel: SimpleViewModel by viewModel { parametersOf(ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertEquals(getViewModel<SimpleViewModel> { parametersOf(ID) }, simpleViewModel)

        title = "Android MVVM"
        setContentView(R.layout.mvvm_activity)

        supportFragmentManager.beginTransaction()
                .replace(R.id.mvvm_frame, MVVMFragment())
                .commit()

        mvvm_button.setOnClickListener {
            navigateTo<ScopedActivityA>(isRoot = true)
        }
    }
}