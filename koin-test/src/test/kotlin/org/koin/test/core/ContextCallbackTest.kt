package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.core.ScopeCallbacks
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.registerContextCallBack
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.release
import org.koin.test.AutoCloseKoinTest

class ContextCallbackTest : AutoCloseKoinTest() {

    val module = applicationContext {
        module(path = "A") {
            bean { ComponentA() }
        }
    }

    class ComponentA

    @Test
    fun `should release context - from B`() {
        startKoin(listOf(module))

        var name = ""

        registerContextCallBack(object : ScopeCallbacks {
            override fun onScopeReleased(path: String) {
                name = path
            }
        })

        release("A")

        Assert.assertEquals("A", name)
    }
}