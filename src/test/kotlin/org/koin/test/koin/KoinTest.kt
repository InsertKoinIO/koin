package org.koin.test.koin

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.koin.Koin
import org.koin.test.ServiceA
import org.koin.test.ServiceB
import org.koin.test.ServiceC
import org.koin.test.koin.example.SampleModuleA
import org.koin.test.koin.example.SampleModuleAC
import org.koin.test.koin.example.SampleModuleB
import org.mockito.Mockito
import org.mockito.Mockito.times

/**
 * Created by arnaud on 31/05/2017.
 */
class KoinTest {

    @Test
    fun `simple load and retrieve instance`() {
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB = ctx.get<ServiceB>()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceA = ctx.getOrNull<ServiceA>()

        assertNotNull(serviceB)
        assertNull(serviceA)
    }

    @Test
    fun `load mulitple definition and retrieve them`() {
        val ctx = Koin().build(SampleModuleAC::class)

        val serviceB = ctx.get<ServiceB>()
        val serviceA = ctx.getOrNull<ServiceA>()
        val serviceC = ctx.getOrNull<ServiceC>()

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(3, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotNull(serviceB)
        assertNotNull(serviceA)
        assertNotNull(serviceC)
    }


    @Test
    fun `functional declaration with mock`() {
        //onLoad only ServiceB
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB: ServiceB = Mockito.mock(ServiceB::class.java)

        ctx.provide { ServiceA(serviceB) }
        Mockito.`when`(serviceB.doSomething()).then {
            println("done B Mock")
        }

        val serviceB_fromDI = ctx.get<ServiceB>()
        val serviceA = ctx.getOrNull<ServiceA>()!!
        serviceA.doSomethingWithB()

        assertNotNull(serviceB_fromDI)
        assertNotNull(serviceA)
        Mockito.verify(serviceB).doSomething()
    }

    @Test
    fun `factory declaration with mock`() {
        //onLoad only ServiceB
        val ctx = Koin().build(SampleModuleB::class)

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)


        val serviceB: ServiceB = Mockito.mock(ServiceB::class.java)
        Mockito.`when`(serviceB.doSomething()).then {
            println("done B Mock")
        }

        ctx.provide { serviceB }
        ctx.factory({ ServiceA(ctx.get()) })

        val serviceA_1 = ctx.get<ServiceA>()
        serviceA_1.doSomethingWithB()

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(2, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceA_2 = ctx.get<ServiceA>()
        serviceA_2.doSomethingWithB()

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(2, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotEquals(serviceA_1, serviceA_2)
        Mockito.verify(serviceB, times(2)).doSomething()
    }

    @Test
    fun `use of factory instances`() {
        //onLoad only ServiceB
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB: ServiceB = Mockito.mock(ServiceB::class.java)
        Mockito.`when`(serviceB.doSomething()).then {
            println("done B Mock")
        }
        ctx.provide { serviceB }
        ctx.factory(ServiceA::class)

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceA_1 = ctx.get<ServiceA>()
        serviceA_1.doSomethingWithB()

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(2, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceA_2 = ctx.get<ServiceA>()
        serviceA_2.doSomethingWithB()

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(2, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotEquals(serviceA_1, serviceA_2)
        Mockito.verify(serviceB, times(2)).doSomething()
    }

    @Test
    fun `use of EmptyModule`() {
        //onLoad only ServiceB
        val ctx = Koin().build()

        assertEquals(0, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        ctx.provide { ServiceB() }

        val serviceB = ctx.get<ServiceB>()

        assertNotNull(serviceB)

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `lazy linking and import definitions`() {
        val ctx = Koin().build()

        assertEquals(0, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val origin = ServiceB()
        ctx.provide { origin }

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        ctx.import(SampleModuleA::class)

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceB = ctx.get<ServiceB>()
        val serviceA = ctx.get<ServiceA>()

        Assert.assertEquals(origin, serviceB)
        Assert.assertNotNull(serviceB)
        Assert.assertNotNull(serviceA)

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(2, ctx.beanRegistry.instanceFactory.instances.size)
    }

}