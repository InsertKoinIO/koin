package org.koin.test.koin

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.Koin
import org.koin.test.koin.example.SampleModuleB
import org.koin.test.koin.example.ServiceA
import org.koin.test.koin.example.ServiceB

/**
 * Created by arnaud on 31/05/2017.
 */
class FactoryTest {

    @Test
    fun `simple factory declaration`() {
        val ctx = Koin().build()

        ctx.factory { ServiceB() }

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val servB_1 = ctx.get<ServiceB>()
        val servB_2 = ctx.get<ServiceB>()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
        assertNotEquals(servB_1, servB_2)
    }

    @Test
    fun `factory declaration with module injection`() {
        val ctx = Koin().build(SampleModuleB::class)

        ctx.factory { ServiceA(ctx.get()) }

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val servA_1 = ctx.get<ServiceA>()
        val servA_2 = ctx.get<ServiceA>()

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotEquals(servA_1, servA_2)
        assertEquals(servA_1.serviceB, servA_1.serviceB)
    }

}