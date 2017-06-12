package org.koin.test.koin

import org.junit.Assert.*
import org.junit.Test
import org.koin.Koin
import org.koin.test.ServiceA
import org.koin.test.ServiceB
import org.koin.test.ServiceC
import org.koin.test.koin.example.SampleModuleAC
import org.koin.test.koin.example.SampleModuleB
import org.mockito.Mockito
import org.mockito.Mockito.times

/**
 * Created by arnaud on 31/05/2017.
 */
class KoinTest {

    @Test
    fun simple_di() {
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB = ctx.get<ServiceB>()
        val serviceA = ctx.getOrNull<ServiceA>()

        assertNotNull(serviceB)
        assertNull(serviceA)
    }

    @Test
    fun simple_remove() {
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB = ctx.get<ServiceB>()
        assertNotNull(serviceB)

        ctx.remove(ServiceB::class)
        val serviceB_2 = ctx.getOrNull<ServiceB>()
        assertNull(serviceB_2)

        assertEquals(0, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun simple_di_multi_modules() {
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
    fun functional_di() {
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
    fun factory_functional() {
        //onLoad only ServiceB
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB: ServiceB = Mockito.mock(ServiceB::class.java)
        Mockito.`when`(serviceB.doSomething()).then {
            println("done B Mock")
        }
        ctx.factory({ ServiceA(serviceB) })

        val serviceA_1 = ctx.get<ServiceA>()
        serviceA_1.doSomethingWithB()
        val serviceA_2 = ctx.get<ServiceA>()
        serviceA_2.doSomethingWithB()

        assertNotEquals(serviceA_1, serviceA_2)
        Mockito.verify(serviceB, times(2)).doSomething()
    }

    @Test
    fun factory() {
        //onLoad only ServiceB
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB: ServiceB = Mockito.mock(ServiceB::class.java)
        Mockito.`when`(serviceB.doSomething()).then {
            println("done B Mock")
        }
        ctx.provide { serviceB }
        ctx.factory(ServiceA::class)

        val serviceA_1 = ctx.get<ServiceA>()
        serviceA_1.doSomethingWithB()
        val serviceA_2 = ctx.get<ServiceA>()
        serviceA_2.doSomethingWithB()

        assertNotEquals(serviceA_1, serviceA_2)
        Mockito.verify(serviceB, times(2)).doSomething()
    }

    @Test
    fun empty_module() {
        //onLoad only ServiceB
        val ctx = Koin().build()

        ctx.provide { ServiceB() }

        val serviceB = ctx.get<ServiceB>()

        assertNotNull(serviceB)
    }

}