package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.error.BeanInstanceCreationException
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.dryRun
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class DryRunTest : AutoCloseKoinTest() {

    val SimpleModule = applicationContext {
        bean { ComponentA() }
        bean { ComponentB(get()) }
    }

    val BrokenModule = applicationContext {
        bean { ComponentB(get()) }
    }

    class ComponentA()
    class ComponentB(val componentA: ComponentA)

    @Test
    fun `successful dry run`() {
        startKoin(listOf(SimpleModule))
        dryRun()

        assertDefinitions(2)
        assertRemainingInstances(2)

        Assert.assertNotNull(get<ComponentA>())
        Assert.assertNotNull(get<ComponentB>())

        assertRemainingInstances(2)
        assertContexts(1)
    }

    @Test
    fun `unsuccessful dry run`() {
        try {
            startKoin(listOf(BrokenModule))
            dryRun()
            fail()
        } catch (e: BeanInstanceCreationException) {
            System.err.println(e)
        }

        assertDefinitions(1)
        assertRemainingInstances(0)

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
        }
        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
        }

        assertRemainingInstances(0)
        assertContexts(1)
    }
}