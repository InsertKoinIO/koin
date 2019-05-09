package org.koin.sample.android.dynamic

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.dynamic_activity.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.android.components.dynamic.DynScoped
import org.koin.sample.android.components.dynamic.DynSingle
import org.koin.sample.android.di.dynamicModule

class DynamicActivity : AppCompatActivity() {

    val single: DynSingle = get()
    val scope: DynScoped = getKoin().createScope("id", named("dynamic_scope")).get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Dynamic Activity"
        setContentView(R.layout.dynamic_activity)

        dyn_button.setOnClickListener {

            unloadKoinModules(dynamicModule)
            loadKoinModules(dynamicModule)
            dyn_button.visibility = View.GONE

            assert(single != get<DynSingle>())
            val scope1 = getKoin().createScope("id", named("dynamic_scope"))
            assert(scope != scope1.get<DynScoped>())

            dyn_label.text = "reload ok!"
        }
    }
}