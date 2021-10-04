package org.koin.sample.androidx.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.androidx.components.APP_TITLE
import org.koin.sample.androidx.components.main.DUMB_SERVICE
import org.koin.sample.androidx.components.main.RandomId
import org.koin.sample.androidx.components.main.SERVICE_IMPL
import org.koin.sample.androidx.components.main.SimpleService
import org.koin.sample.androidx.mvp.MVPActivity
import org.koin.sample.androidx.utils.navigateTo

class MainActivity : AppCompatActivity() {

    // Inject by Interface - default definition
    val service: SimpleService by inject()

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

        setContentView(R.layout.main_activity)
        title = "Android First Samples"

        main_button.setOnClickListener {
            navigateTo<MVPActivity>(isRoot = true)
        }
    }
}