package org.koin.sample.androidx.mvp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.mvp_activity.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.sample.android.R
import org.koin.sample.androidx.components.ID
import org.koin.sample.androidx.components.mvp.FactoryPresenter
import org.koin.sample.androidx.components.mvp.ScopedPresenter
import org.koin.sample.androidx.mvvm.MVVMActivity
import org.koin.sample.androidx.utils.navigateTo

class MVPActivity : AppCompatActivity(R.layout.mvp_activity), AndroidScopeComponent {

    override val scope: Scope by activityRetainedScope()

    // Inject presenter as Factory
    val factoryPresenter: FactoryPresenter by inject { parametersOf(ID) }

    // Inject presenter from MVPActivity's scope
    val scopedPresenter: ScopedPresenter by inject { parametersOf(ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertEquals(factoryPresenter.id, scopedPresenter.id)
        assertEquals(factoryPresenter.service, scopedPresenter.service)

        assertNotEquals(get<FactoryPresenter> { parametersOf(ID) }, factoryPresenter)
        assertEquals(scope.get<ScopedPresenter>(), scopedPresenter)

        title = "Android MVP"

        mvp_button.setOnClickListener {
            navigateTo<MVVMActivity>(isRoot = true)
        }

        val my_int = 42
        getKoin().setProperty("my_int", my_int)
        assertEquals(my_int, getKoin().getProperty<Int>("my_int"))
    }

}