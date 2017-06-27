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

        ctx.assertSizes(2, 0)

        val serviceA_1 = ctx.get<ServiceA>()
        serviceA_1.doSomethingWithB()

        val serviceA_2 = ctx.get<ServiceA>()
        serviceA_2.doSomethingWithB()

        ctx.assertSizes(2, 2)

        assertEquals(serviceA_1, serviceA_2)
        Mockito.verify(serviceB, times(2)).doSomething()
    }

    @Test
    fun `class providing component`() {
        val ctx = Koin().build()

        ctx.provide(ServiceB::class)
        ctx.provide(ServiceA::class)

        ctx.assertSizes(2, 0)

        assertNotNull(ctx.get<ServiceB>())
        assertNotNull(ctx.get<ServiceA>())

        ctx.assertSizes(2, 2)

    }

    @Test
    fun `missing bean component`() {
        val ctx = Koin().build()

        ctx.provide(ServiceB::class)

        assertNull(ctx.getOrNull<ServiceA>())
        ctx.assertSizes(1, 0)
    }

    @Test
    fun `unsafe get with missing bean component`() {
        val ctx = Koin().build()

        try {
            assertNull(ctx.get<ServiceA>())
        } catch(e: NoBeanDefFoundException) {
            assertNotNull(e)
        }

        ctx.assertSizes(0, 0)
    }

    @Test
    fun `provide for class with many constructors`() {
        val ctx = Koin().build()

        ctx.provide(ServiceB::class)
        ctx.provide(ServiceA::class)
        ctx.provide(ServiceManyConstructor::class)

        assertNotNull(ctx.get<ServiceManyConstructor>())
        ctx.assertSizes(3, 3)
    }

    @Test
    fun `provide for class with no constructor`() {
        val ctx = Koin().build()

        ctx.provide(NoConstructor::class)

        assertNotNull(ctx.get<NoConstructor>())
        ctx.assertSizes(1, 1)
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
    fun `use of EmptyModule`() {
        //onLoad only ServiceB
        val ctx = Koin().build()

        ctx.assertSizes(0, 0)

        ctx.provide { ServiceB() }

        val serviceB = ctx.get<ServiceB>()

        assertNotNull(serviceB)

        ctx.assertSizes(1, 1)
    }

    @Test
    fun `lazy linking and import definitions`() {
        val ctx = Koin().build()

        ctx.assertSizes(0, 0)

        ctx.import(SampleModuleA::class)

        ctx.assertSizes(1, 0)

        ctx.provide { ServiceB() }
        Assert.assertNotNull(ctx.get<ServiceA>())
        Assert.assertNotNull(ctx.get<ServiceB>())

        ctx.assertSizes(2, 2)
    }

}