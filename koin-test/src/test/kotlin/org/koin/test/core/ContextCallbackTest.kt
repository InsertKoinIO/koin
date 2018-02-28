package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.ContextCallback
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.registerContextCallBack
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.releaseContext
import org.koin.test.AutoCloseKoinTest

class ContextCallbackTest : AutoCloseKoinTest() {

    val module = applicationContext {
        context(name = "A") {
            bean { ComponentA() }
        }
    }

    class ComponentA

    @Test
    fun `should release context - from B`() {
        startKoin(listOf(module))

        var name = ""

        registerContextCallBack(object : ContextCallback {
            override fun onContextReleased(contextName: String) {
                name = contextName
            }
        })

        releaseContext("A")

        Assert.assertEquals("A", name)
    }
}