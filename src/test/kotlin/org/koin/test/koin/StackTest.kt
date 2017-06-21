package org.koin.test.koin

import org.junit.Assert.*
import org.junit.Test
import org.koin.Koin
import org.koin.test.koin.example.ServiceA
import org.koin.test.koin.example.SampleModuleB


/**
 * Created by arnaud on 31/05/2017.
 */
class StackTest {

    @Test
    fun `stack a definition and retrieve it`() {
        val ctx = Koin().build(SampleModuleB::class)
        ctx.stack { ServiceA(ctx.get()) }

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceA_1: ServiceA = ctx.get()
        val serviceA_2: ServiceA? = ctx.getOrNull()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotNull(serviceA_1)
        assertNull(serviceA_2)
    }

    @Test
    fun `stack same definitions`() {
        val ctx = Koin().build(SampleModuleB::class)

        ctx.stack { ServiceA(ctx.get()) }

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceA_1: ServiceA = ctx.get()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        ctx.stack { ServiceA(ctx.get()) }

        val serviceA_2: ServiceA = ctx.get()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceA_3: ServiceA? = ctx.getOrNull()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotNull(serviceA_1)
        assertNotNull(serviceA_2)
        assertNull(serviceA_3)
        assertNotEquals(serviceA_1, serviceA_2)
    }
}