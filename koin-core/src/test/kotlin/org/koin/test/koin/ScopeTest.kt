package org.koin.test.koin

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.error.BeanDefinitionException
import org.koin.test.ext.*
import org.koin.test.koin.example.*

/**
 * Created by arnaud on 31/05/2017.
 */
class ScopeTest {

    @Test
    fun `provide at scope `() {
        val ctx = Koin().build()
        ctx.declareScope(ServiceB::class)
        ctx.provideAt({ ServiceB() }, ServiceB::class)
        ctx.assertScopes(2)
        ctx.assertSizes(1, 0)
        Assert.assertNotNull(ctx.get<ServiceB>())

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertSizes(1, 1)
    }

    @Test
    fun `get scoped instances`() {
        val ctx = Koin().build(ScopedModuleB())
        ctx.assertScopes(2)
        ctx.assertSizes(1, 0)
        Assert.assertNotNull(ctx.get<ServiceB>())

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertSizes(1, 1)
    }

    @Test
    fun `isolated scope - 1 instance`() {
        val ctx = Koin().build(ScopedModuleB())
        ctx.assertScopes(2)
        ctx.assertScopeSize(ServiceB::class, 0)
        ctx.assertSizes(1, 0)
        ctx.assertRootScopeSize(0)
        Assert.assertNotNull(ctx.getOrNull<ServiceB>())

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertSizes(1, 1)
        ctx.assertRootScopeSize(0)
    }

    @Test
    fun `multi scope - remove test`() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA())

        val serviceB_1 = ctx.get<ServiceB>()
        var serviceA = ctx.get<ServiceA>()

        Assert.assertEquals(serviceA.serviceB, serviceB_1)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(2, 2)
        ctx.assertRootScopeSize(0)

        ctx.release(serviceB_1)
        ctx.assertScopeSize(ServiceB::class, 0)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(2, 1)
        ctx.assertRootScopeSize(0)

        val serviceB_2 = ctx.get<ServiceB>()
        serviceA = ctx.get<ServiceA>()
        Assert.assertNotEquals(serviceA.serviceB, serviceB_2)
        Assert.assertNotEquals(serviceB_1, serviceB_2)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(2, 2)
        ctx.assertRootScopeSize(0)

        ctx.release(serviceA)
        serviceA = ctx.get<ServiceA>()
        Assert.assertEquals(serviceA.serviceB, serviceB_2)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(2, 2)
        ctx.assertRootScopeSize(0)
    }

    @Test
    fun `get multi scoped instances`() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA())
        ctx.assertScopes(3)
        ctx.assertSizes(2, 0)
        Assert.assertNotNull(ctx.get<ServiceB>())
        Assert.assertNotNull(ctx.get<ServiceA>())

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(2, 2)
    }

    @Test
    fun `get multi scoped instances with root`() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA(), SampleModuleC())
        ctx.assertScopes(3)
        ctx.assertSizes(3, 0)
        Assert.assertNotNull(ctx.get<ServiceC>())

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertRootScopeSize(1)
        ctx.assertSizes(3, 3)
    }

    @Test
    fun `isolated scope - 3 instance`() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA(), SampleModuleC())
        ctx.assertScopes(3)
        ctx.assertRootScopeSize(0)
        ctx.assertSizes(3, 0)

        val serviceB = ctx.get<ServiceB>()
        Assert.assertNotNull(serviceB)
        ctx.assertScopes(3)
        ctx.assertRootScopeSize(0)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertSizes(3, 1)

        val serviceA = ctx.get<ServiceA>()
        Assert.assertNotNull(serviceA)
        ctx.assertScopes(3)
        ctx.assertRootScopeSize(0)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(3, 2)

        val serviceC = ctx.get<ServiceC>()
        Assert.assertNotNull(serviceC)
        ctx.assertScopes(3)
        ctx.assertRootScopeSize(1)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(3, 3)

        Assert.assertEquals(serviceB, serviceA.serviceB)
        Assert.assertEquals(serviceB, serviceC.serviceB)
        Assert.assertEquals(serviceA, serviceC.serviceA)
    }

    @Test
    fun `scope release`() {
        val ctx = Koin().build(ScopedModuleB())
        ctx.assertScopes(2)
        ctx.assertSizes(1, 0)
        val serviceB = ctx.get<ServiceB>()
        Assert.assertNotNull(serviceB)
        ctx.assertSizes(1, 1)
        ctx.assertScopeSize(ServiceB::class, 1)

        ctx.release(serviceB)
        ctx.assertSizes(1, 0)
        ctx.assertScopeSize(ServiceB::class, 0)
    }

    @Test
    fun `scope release from object`() {
        val ctx = Koin().build(ScopedModuleB())
        ctx.assertScopes(2)
        ctx.assertSizes(1, 0)
        val serviceB = ctx.get<ServiceB>()
        Assert.assertNotNull(serviceB)
        ctx.assertSizes(1, 1)
        ctx.assertScopeSize(ServiceB::class, 1)

        ctx.release(serviceB)
        ctx.assertSizes(1, 0)
        ctx.assertScopeSize(ServiceB::class, 0)
    }

    @Test
    fun `provide bean in non existing scope `() {
        val ctx = Koin().build()
        try {
            ctx.provideAt({ ServiceB() }, ServiceB::class)
            Assert.fail()
        } catch (e: BeanDefinitionException) {
        }

        try {
            ctx.assertScopeSize(ServiceB::class, 0)
        } catch (e: Exception) {
        }
        ctx.assertSizes(0, 0)
    }
}