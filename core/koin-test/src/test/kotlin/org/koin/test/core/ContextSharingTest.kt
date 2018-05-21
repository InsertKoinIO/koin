package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class ContextSharingTest : AutoCloseKoinTest() {
    val module1 = module {
        single { ComponentA() }
    }

    val module2 = module {
        single { ComponentB() }
    }

    class ComponentA
    class ComponentB

    @Test
    fun `allow context sharing`() {
        startKoin(listOf(module1))
        loadKoinModules(module2)

        assertDefinitions(2)
        assertContexts(1)

        Assert.assertNotNull(get<ComponentA>())
        Assert.assertNotNull(get<ComponentB>())
        assertRemainingInstances(2)
    }

    @Test
    fun `allow context not sharing`() {
        startKoin(listOf(module1))
        try {
            startKoin(listOf(module2))
        } catch (e: Exception) {
            assertNotNull(e)
        }

        assertDefinitions(1)
        assertContexts(1)

        Assert.assertNotNull(get<ComponentA>())
        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
        }
        assertRemainingInstances(1)
    }
}