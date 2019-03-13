package org.koin.sample.android.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.android.components.*
import org.koin.sample.android.components.main.DUMB_SERVICE
import org.koin.sample.android.components.main.RandomId
import org.koin.sample.android.components.main.SERVICE_IMPL
import org.koin.sample.android.components.main.Service
import org.koin.sample.android.mvp.MVPActivity
import org.koin.sample.android.utils.navigateTo

class MainActivity : AppCompatActivity() {

    // Inject by Interface - default definition
    val service: Service by inject()

    // Inject by Interface - qualified definition with a String name
    val dumbService: Service by inject(named("dumb"))

    // Inject factory
    val randomId: RandomId by inject()

    // Inject property from koin.properties
    val propertyTitle: String? = getKoin().getProperty("app.title")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertEquals(SERVICE_IMPL, service.id)
        assertEquals(DUMB_SERVICE, dumbService.id)
        assertNotEquals(get<RandomId>().id, randomId.id)
        assertEquals(APP_TITLE, propertyTitle)

        setContentView(R.layout.main_activity)
        title = "Android First Samples"

        main_button.setOnClickListener {
            navigateTo<MVPActivity>()
        }
    }
}