package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertRemainingInstances

class ParametersLazyInstanceTest : AutoCloseKoinTest() {

    val module = applicationContext {
        bean { params -> ComponentA(params[URL]) }
    }

    class ComponentA(val url: String)
    class ComponentB : KoinComponent {

        lateinit var url: String

        val a: ComponentA by inject { mapOf(URL to url) }

        fun getAWithUrl(url: String) {
            this.url = url
            println("Got A : $a")
        }
    }

    @Test
    fun `should inject parameters with factory`() {
        startKoin(listOf(module))

        val url = "https://..."
        val b = ComponentB()

        assertRemainingInstances(0)
        b.getAWithUrl(url)

        assertRemainingInstances(1)
        Assert.assertEquals(url, b.a.url)
    }

    companion object {
        const val URL = "URL"
    }
}