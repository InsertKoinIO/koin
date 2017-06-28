package org.koin.test.koin

import org.junit.Assert
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.Koin
import org.koin.test.koin.example.*

/**
 * Created by arnaud on 31/05/2017.
 */
class DeleteAndRemoveTest {

    @Test
    fun `delete an instance`() {
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB = ctx.get<ServiceB>()
        assertNotNull(serviceB)

        ctx.assertSizes(1, 1)

        ctx.delete(ServiceB::class)

        ctx.assertSizes(1, 0)
    }

    @Test
    fun `delete & recreate instance`() {
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB = ctx.get<ServiceB>()
        assertNotNull(serviceB)

        ctx.assertSizes(1, 1)

        ctx.delete(ServiceB::class)

        ctx.assertSizes(1, 0)

        ctx.provide { ServiceB() }

        val serviceB2 = ctx.get<ServiceB>()

        ctx.assertSizes(1, 1)

        assertNotEquals(serviceB, serviceB2)
    }

    @Test
    fun `delete multiple instances`() {
        val ctx = Koin().build(SampleModuleB::class, SampleModuleA_C::class)

        val serviceB = ctx.get<ServiceB>()
        val serviceC = ctx.get<ServiceC>()
        assertNotNull(serviceB)
        assertNotNull(serviceC)

        ctx.assertSizes(3, 3)

        ctx.delete(ServiceB::class, ServiceC::class, ServiceA::class)

        ctx.assertSizes(3, 0)
    }


    @Test
    fun `remove bean definition`() {
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB = ctx.get<ServiceB>()
        assertNotNull(serviceB)

        ctx.remove(ServiceB::class)
        val serviceB_2 = ctx.getOrNull<ServiceB>()
        Assert.assertNull(serviceB_2)

        ctx.assertSizes(0, 0)
    }

    @Test
    fun `remove multiple definitions`() {
        val ctx = Koin().build(SampleModuleB::class, SampleModuleA_C::class)

        val serviceB = ctx.get<ServiceB>()
        val serviceC = ctx.get<ServiceC>()
        assertNotNull(serviceB)
        assertNotNull(serviceC)

        ctx.assertSizes(3, 3)

        ctx.remove(ServiceB::class, ServiceC::class, ServiceA::class)

        ctx.assertSizes(0, 0)
    }

}