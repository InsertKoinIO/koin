package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.core.scope.ScopeCallbacks
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.registerScopeCallBack
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.release
import org.koin.test.AutoCloseKoinTest

class ContextCallbackTest : AutoCloseKoinTest() {

    val module = module {
        module(path = "A") {
            single { ComponentA() }
        }
    }

    class ComponentA

    @Test
    fun `should release context - from B`() {
        startKoin(listOf(module))

        var name = ""

        registerScopeCallBack(object : ScopeCallbacks {
            override fun onScopeReleased(path: String) {
                name = path
            }
        })

        release("A")

        Assert.assertEquals("A", name)
    }
}