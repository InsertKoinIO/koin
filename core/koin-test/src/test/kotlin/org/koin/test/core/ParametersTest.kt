package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.dryRun
import org.koin.test.ext.junit.assertRemainingInstances

class ParametersTest : AutoCloseKoinTest() {

    val simpleModule1 = module {

        factory { params -> ComponentA(params[PARAM_URL]) }
    }

    val simpleModule2 = module {

        single { params -> ComponentA(params[PARAM_URL]) }
    }

    val simpleModule3 = module {

        single { params -> ComponentA(params.getOrNUll(PARAM_URL) ?: DEFAULT_URL) }
    }

    class ComponentA(val url: String)

    class Component1 : KoinComponent {

        init {
            println("Ctor Component1")
        }

        val compA: ComponentA by inject { mapOf(PARAM_URL to URL1) }
    }

    class Component2 : KoinComponent {

        init {
            println("Ctor Component2")
        }

        val compA: ComponentA by inject { mapOf(PARAM_URL to URL2) }
    }

    class Component3 : KoinComponent {

        init {
            println("Ctor Component3")
        }

        val compA: ComponentA by inject()
    }

    @Test
    fun `should inject parameters with factory`() {
        startKoin(listOf(simpleModule1))

        val c1 = Component1()
        val c2 = Component2()

        assertRemainingInstances(0)

        Assert.assertEquals(URL1, c1.compA.url)
        Assert.assertEquals(URL2, c2.compA.url)
    }

    @Test
    fun `should inject parameters with bean`() {
        startKoin(listOf(simpleModule2))

        val c1 = Component1()
        val c2 = Component2()

        assertRemainingInstances(0)

        Assert.assertEquals(URL1, c1.compA.url)
        Assert.assertEquals(URL1, c2.compA.url)
    }

    @Test
    fun `should dry run default parameters`() {
        startKoin(listOf(simpleModule1))

        dryRun { mapOf(PARAM_URL to "DEFAULT") }
    }

    @Test
    fun `should inject default params with bean`() {
        startKoin(listOf(simpleModule3))

        val c1 = Component3()

        assertRemainingInstances(0)

        Assert.assertEquals(DEFAULT_URL, c1.compA.url)
    }

    companion object {
        const val PARAM_URL = "URL"
        const val URL1 = "URL_1"
        const val URL2 = "URL_2"
        const val DEFAULT_URL = "DEFAULT"
    }

}