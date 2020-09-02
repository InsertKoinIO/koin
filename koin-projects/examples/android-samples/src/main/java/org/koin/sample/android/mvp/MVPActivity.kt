package org.koin.sample.android.mvp

import android.os.Bundle
import kotlinx.android.synthetic.main.mvp_activity.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.koin.android.scope.ScopeActivity
import org.koin.core.parameter.parametersOf
import org.koin.sample.android.R
import org.koin.sample.android.components.ID
import org.koin.sample.android.components.mvp.FactoryPresenter
import org.koin.sample.android.components.mvp.ScopedPresenter
import org.koin.sample.android.mvvm.MVVMActivity
import org.koin.sample.android.utils.navigateTo

class MVPActivity : ScopeActivity() {

    // Inject presenter as Factory
    val factoryPresenter: FactoryPresenter by inject { parametersOf(ID) }

    // Inject presenter from MVPActivity's scope
    val scopedPresenter: ScopedPresenter by inject { parametersOf(ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertEquals(factoryPresenter.id, scopedPresenter.id)
        assertEquals(factoryPresenter.service, scopedPresenter.service)

        assertNotEquals(get<FactoryPresenter> { parametersOf(ID) }, factoryPresenter)
        assertEquals(get<ScopedPresenter>(), scopedPresenter)

        setContentView(R.layout.mvp_activity)
        title = "Android MVP"

        mvp_button.setOnClickListener {
            navigateTo<MVVMActivity>(isRoot = true)
        }
    }
}