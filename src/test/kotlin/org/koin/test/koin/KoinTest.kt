package org.koin.test.koin

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.koin.Koin
import org.koin.error.NoBeanDefFoundException
import org.koin.test.koin.example.*
import org.mockito.Mockito
import org.mockito.Mockito.times

/**
 * Created by arnaud on 31/05/2017.
 */
class KoinTest {

    @Test
    fun `functional provide component`() {
        val ctx = Koin().build()

        val serviceB: ServiceB = Mockito.mock(ServiceB::class.java)
        Mockito.`when`(serviceB.doSomething()).then {
            println("done B Mock")
        }

        ctx.provide { serviceB }
        ctx.provide({ ServiceA(ctx.get()) })

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceA_1 = ctx.get<ServiceA>()
        serviceA_1.doSomethingWithB()

        val serviceA_2 = ctx.get<ServiceA>()
        serviceA_2.doSomethingWithB()

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(2, ctx.beanRegistry.instanceFactory.instances.size)

        assertEquals(serviceA_1, serviceA_2)
        Mockito.verify(serviceB, times(2)).doSomething()
    }

    @Test
    fun `class providing component`() {
        val ctx = Koin().build()

        ctx.provide(ServiceB::class)
        ctx.provide(ServiceA::class)

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotNull(ctx.get<ServiceB>())
        assertNotNull(ctx.get<ServiceA>())

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(2, ctx.beanRegistry.instanceFactory.instances.size)

    }

    @Test
    fun `missing bean component`() {
        val ctx = Koin().build()

        ctx.provide(ServiceB::class)

        assertNull(ctx.getOrNull<ServiceA>())
        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `unsafe get with missing bean component`() {
        val ctx = Koin().build()

        try {
            assertNull(ctx.get<ServiceA>())
        } catch(e: NoBeanDefFoundException) {
            assertNotNull(e)
        }

        assertEquals(0, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `missing bean component - lazy linking`() {
        val ctx = Koin().build(SampleModuleC::class)

        assertNull(ctx.getOrNull<ServiceA>())
        assertNull(ctx.getOrNull<ServiceC>())
        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `functional declaration and inject mock`() {
        val ctx = Koin().build()

        val serviceB: ServiceB = Mockito.mock(ServiceB::class.java)

        ctx.provide { ServiceA(serviceB) }
        Mockito.`when`(serviceB.doSomething()).then {
            println("done B Mock")
        }

        val serviceA = ctx.get<ServiceA>()
        serviceA.doSomethingWithB()

        assertNotNull(serviceA)
        Mockito.verify(serviceB).doSomething()
    }

    @Test
    fun `factory declaration with mock`() {
        val ctx = Koin().build()

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

        ctx.import(SampleModuleA::class)

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        ctx.provide { ServiceB() }
        Assert.assertNotNull(ctx.get<ServiceA>())
        Assert.assertNotNull(ctx.get<ServiceB>())

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(2, ctx.beanRegistry.instanceFactory.instances.size)
    }

}