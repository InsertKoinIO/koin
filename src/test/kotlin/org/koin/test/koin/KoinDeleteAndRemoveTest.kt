package org.koin.test.koin

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.koin.Koin
import org.koin.test.ServiceA
import org.koin.test.ServiceB
import org.koin.test.ServiceC
import org.koin.test.koin.example.SampleModuleAC
import org.koin.test.koin.example.SampleModuleB

/**
 * Created by arnaud on 31/05/2017.
 */
class KoinDeleteAndRemoveTest {

    @Test
    fun simple_delete() {
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB = ctx.get<ServiceB>()
        assertNotNull(serviceB)

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        ctx.delete(ServiceB::class)

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun create_delete() {
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB = ctx.get<ServiceB>()
        assertNotNull(serviceB)

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        ctx.delete(ServiceB::class)

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        ctx.provide(ServiceB::class)

        val serviceB2 = ctx.get<ServiceB>()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotEquals(serviceB, serviceB2)
    }

    @Test
    fun multi_delete() {
        val ctx = Koin().build(SampleModuleAC::class)

        val serviceB = ctx.get<ServiceB>()
        val serviceC = ctx.get<ServiceC>()
        assertNotNull(serviceB)
        assertNotNull(serviceC)

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(3, ctx.beanRegistry.instanceFactory.instances.size)

        ctx.delete(ServiceB::class, ServiceC::class, ServiceA::class)

        assertEquals(3, ctx.beanRegistry.definitions.size)

        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
    }


    @Test
    fun simple_remove() {
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB = ctx.get<ServiceB>()
        assertNotNull(serviceB)

        ctx.remove(ServiceB::class)
        val serviceB_2 = ctx.getOrNull<ServiceB>()
        Assert.assertNull(serviceB_2)

        assertEquals(0, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun multi_remove() {
        val ctx = Koin().build(SampleModuleAC::class)

        val serviceB = ctx.get<ServiceB>()
        val serviceC = ctx.get<ServiceC>()
        assertNotNull(serviceB)
        assertNotNull(serviceC)

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(3, ctx.beanRegistry.instanceFactory.instances.size)

        ctx.remove(ServiceB::class, ServiceC::class, ServiceA::class)

        assertEquals(0, ctx.beanRegistry.definitions.size)

        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
    }

}