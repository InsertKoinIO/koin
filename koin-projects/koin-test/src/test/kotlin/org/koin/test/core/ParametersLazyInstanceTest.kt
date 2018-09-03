package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertRemainingInstanceHolders

class ParametersLazyInstanceTest : AutoCloseKoinTest() {

    val module = module {
        single { (url: String) -> ComponentA(url) }
    }

    class ComponentA(val url: String)
    class ComponentB : KoinComponent {

        lateinit var url: String

        val a: ComponentA by inject { parametersOf(url) }

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

        assertRemainingInstanceHolders(0)
        b.getAWithUrl(url)

        assertRemainingInstanceHolders(1)
        Assert.assertEquals(url, b.a.url)
    }
}