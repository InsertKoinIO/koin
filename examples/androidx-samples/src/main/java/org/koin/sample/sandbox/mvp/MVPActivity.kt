package org.koin.sample.sandbox.mvp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.sample.sandbox.R
import org.koin.sample.sandbox.components.ID
import org.koin.sample.sandbox.components.mvp.FactoryPresenter
import org.koin.sample.sandbox.components.mvp.ScopedPresenter
import org.koin.sample.sandbox.mvvm.MVVMActivity
import org.koin.sample.sandbox.utils.navigateTo

class MVPActivity : AppCompatActivity(R.layout.mvp_activity), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    // Inject presenter as Factory
    val factoryPresenter: FactoryPresenter by inject { parametersOf(ID) }

    // Inject presenter from MVPActivity's scope
    val scopedPresenter: ScopedPresenter by inject { parametersOf(ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assert(factoryPresenter.id == scopedPresenter.id)
        assert(factoryPresenter.service == scopedPresenter.service)

        assert(get<FactoryPresenter> { parametersOf(ID) } != factoryPresenter)
        assert(scope.get<ScopedPresenter>() == scopedPresenter)

        title = "Android MVP"

        findViewById<Button>(R.id.mvp_button).setOnClickListener {
            navigateTo<MVVMActivity>(
                isRoot = true, extras =
                mapOf(
                    "vm1" to "value to stateViewModel",
                    "id" to "vm1"
                )
            )
        }

        val my_int = 42
        getKoin().setProperty("my_int", my_int)
        assert(my_int == getKoin().getProperty<Int>("my_int"))
    }

}