package org.koin.test.koin

import org.junit.Assert.*
import org.junit.Test
import org.koin.Koin
import org.koin.error.NoBeanDefFoundException
import org.koin.test.koin.example.*


/**
 * Created by arnaud on 31/05/2017.
 */
class LazyTest {

    @Test
    fun `lazy inject into instance`() {
        val ctx = Koin().build(SampleModuleB::class)

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceB by ctx.lazyGet<ServiceB>()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        serviceB.doSomething()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `lazy inject into instances`() {
        val ctx = Koin().build(SampleModuleC_ImportB::class)

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotNull(ctx.get<ServiceB>())
        val serviceC by ctx.lazyGet<ServiceC>()

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        serviceC.doSomethingWithAll()

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(3, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `don't lazy inject into instance`() {
        val ctx = Koin().build()
        ctx.provide { ServiceA(ctx.get()) }

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceA by ctx.lazyGet<ServiceA>()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        try {
            serviceA.doSomethingWithB()
            fail()
        } catch(e: NoBeanDefFoundException) {
            assertNotNull(e)
        }

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
    }
}