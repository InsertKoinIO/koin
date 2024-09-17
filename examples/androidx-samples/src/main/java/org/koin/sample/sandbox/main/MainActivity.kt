package org.koin.sample.sandbox.main

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import org.koin.sample.sandbox.MainApplication
import org.koin.sample.sandbox.R
import org.koin.sample.sandbox.components.APP_TITLE
import org.koin.sample.sandbox.components.main.*
import org.koin.sample.sandbox.mvp.MVPActivity
import org.koin.sample.sandbox.utils.navigateTo

class MainActivity : AppCompatActivity() {

    // Inject by Interface - default definition
    val service: SimpleService by inject()

//    val simplePresenter : SimplePresenter by inject()

    // Inject by Interface - qualified definition with a String name
    val dumbService: SimpleService by inject(named("dumb"))

    // Inject factory
    val randomId: RandomId by inject()

    // Inject property from koin.properties
    val propertyTitle: String? = getKoin().getProperty("app.title")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assert(SERVICE_IMPL == service.id)
        assert(DUMB_SERVICE == dumbService.id)
        assert(get<RandomId>().id != randomId.id)
        assert(APP_TITLE == propertyTitle?.replace("\"", ""))

//        assert(simplePresenter.activity == this)

        setContentView(R.layout.main_activity)
        title = "Android First Samples"

        findViewById<Button>(R.id.main_button).setOnClickListener {
            navigateTo<MVPActivity>(isRoot = true)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val endTime = System.currentTimeMillis()
            val startupTime = endTime - MainApplication.startTime
            Log.i("[MEASURE]","App startup time - $startupTime ms")
        }
    }
}