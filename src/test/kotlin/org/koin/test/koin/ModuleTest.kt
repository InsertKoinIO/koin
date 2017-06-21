package org.koin.test.koin

import org.junit.Assert.*
import org.junit.Test
import org.koin.Koin
import org.koin.test.koin.example.ServiceA
import org.koin.test.koin.example.ServiceB
import org.koin.test.koin.example.ServiceC
import org.koin.test.koin.example.SampleModuleA
import org.koin.test.koin.example.SampleModuleC_ImportB
import org.koin.test.koin.example.SampleModuleB
import org.koin.test.koin.example.SampleModuleC

/**
 * Created by arnaud on 31/05/2017.
 */
class ModuleTest {

    @Test
    fun `simple Module load and retrieve instance`() {
        val ctx = Koin().build(SampleModuleB::class)

        val serviceB = ctx.get<ServiceB>()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceA = ctx.getOrNull<ServiceA>()

        assertNotNull(serviceB)
        assertNull(serviceA)
    }

    @Test
    fun `load mulitple modules`() {
        val ctx = Koin().build(SampleModuleA::class, SampleModuleB::class)

        assertNotNull(ctx.get<ServiceB>())
        assertNotNull(ctx.get<ServiceA>())
        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(2, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `load mulitple modules and overwrite definitions`() {
        val ctx = Koin().build(SampleModuleA::class, SampleModuleC_ImportB::class)

        assertNotNull(ctx.get<ServiceB>())
        assertNotNull(ctx.get<ServiceA>())
        assertNotNull(ctx.get<ServiceC>())
        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(3, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `momdule import another module`() {
        val ctx = Koin().build(SampleModuleC_ImportB::class)

        assertNotNull(ctx.get<ServiceB>())
        assertNotNull(ctx.get<ServiceA>())
        assertNotNull(ctx.get<ServiceC>())
        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(3, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `import with lazy linking`() {
        //onLoad only ServiceB
        val ctx = Koin().build(SampleModuleC::class)

        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        ctx.provide { ServiceB() }

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotNull(ctx.get<ServiceA>())
        assertNotNull(ctx.get<ServiceB>())
        assertNotNull(ctx.get<ServiceC>())
        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(3, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `missing bean component - lazy linking`() {
        val ctx = Koin().build(SampleModuleC::class)

        assertNull(ctx.getOrNull<ServiceA>())
        assertNull(ctx.getOrNull<ServiceC>())
        assertEquals(2, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
    }
}