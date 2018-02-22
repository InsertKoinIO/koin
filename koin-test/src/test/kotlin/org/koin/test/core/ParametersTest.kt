package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.koin.test.dryRun

class ParametersTest : AutoCloseKoinTest() {

    val simpleModule1 = applicationContext {

        factory { params -> ComponentA(params[PARAM_URL]) }
    }

    val simpleModule2 = applicationContext {

        bean { params -> ComponentA(params[PARAM_URL]) }
    }

    class ComponentA(val url: String)

    class Component1 : KoinComponent {
        val compA: ComponentA by inject(parameters = mapOf(PARAM_URL to URL1))
    }

    class Component2 : KoinComponent {
        val compA: ComponentA by inject(parameters = mapOf(PARAM_URL to URL2))
    }

    @Test
    fun `should inject parameters with factory`() {
        startKoin(listOf(simpleModule1))

        val c1 = Component1()
        val c2 = Component2()

        Assert.assertEquals(URL1, c1.compA.url)
        Assert.assertEquals(URL2, c2.compA.url)
    }

    @Test
    fun `should inject parameters with bean`() {
        startKoin(listOf(simpleModule2))

        val c1 = Component1()
        val c2 = Component2()

        Assert.assertEquals(URL1, c1.compA.url)
        Assert.assertEquals(URL1, c2.compA.url)
    }

    @Test
    fun `should dry run default parameters`() {
        startKoin(listOf(simpleModule1))

        dryRun(defaultParameters = mapOf(PARAM_URL to "DEFAULT"))
    }

    companion object {
        const val PARAM_URL = "URL"
        const val URL1 = "URL_1"
        const val URL2 = "URL_2"
    }

}