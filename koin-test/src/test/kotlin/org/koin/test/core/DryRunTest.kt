package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.error.BeanInstanceCreationException
import org.koin.standalone.dryRun
import org.koin.test.KoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances
import org.koin.test.get

class DryRunTest : KoinTest {

    class SimpleModule() : Module() {
        override fun context() = applicationContext {
            provide { ComponentA() }
            provide { ComponentB(get()) }
        }
    }

    class BrokenModule() : Module() {
        override fun context() = applicationContext {
            provide { ComponentB(get()) }
        }
    }

    class ComponentA()
    class ComponentB(val componentA: ComponentA)

    @Test
    fun `successful dry run`() {
        dryRun(listOf(SimpleModule()))

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
            dryRun(listOf(BrokenModule()))
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