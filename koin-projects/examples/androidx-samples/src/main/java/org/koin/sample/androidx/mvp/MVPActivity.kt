package org.koin.sample.androidx.mvp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.mvp_activity.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.koin.androidx.scope.activityScope
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.core.scope.get
import org.koin.core.scope.inject
import org.koin.sample.android.R
import org.koin.sample.androidx.components.ID
import org.koin.sample.androidx.components.mvp.FactoryPresenter
import org.koin.sample.androidx.components.mvp.ScopedPresenter
import org.koin.sample.androidx.mvvm.MVVMActivity
import org.koin.sample.androidx.utils.navigateTo

class MVPActivity : AppCompatActivity(R.layout.mvp_activity), KoinScopeComponent {

    override val scope: Scope by lazy { activityScope() }

    // Inject presenter as Factory
    val factoryPresenter: FactoryPresenter by inject(mode = LazyThreadSafetyMode.NONE) { parametersOf(ID) }

    // Inject presenter from MVPActivity's scope
    val scopedPresenter: ScopedPresenter by inject { parametersOf(ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertEquals(factoryPresenter.id, scopedPresenter.id)
        assertEquals(factoryPresenter.service, scopedPresenter.service)

        assertNotEquals(get<FactoryPresenter> { parametersOf(ID) }, factoryPresenter)
        assertEquals(get<ScopedPresenter>(), scopedPresenter)

        title = "Android MVP"

        mvp_button.setOnClickListener {
            navigateTo<MVVMActivity>(isRoot = true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // don't forget to close current scope if not activityScope() or fragmentScope()
        // closeScope()
    }
}