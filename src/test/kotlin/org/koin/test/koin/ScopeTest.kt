package org.koin.test.koin

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.context.Scope
import org.koin.module.Module
import org.koin.test.ext.assertScopes
import org.koin.test.ext.assertSizes
import org.koin.test.koin.example.ServiceA
import org.koin.test.koin.example.ServiceB


class ScopedModuleB : Module() {
    override fun context() =
            declareContext {
                scope { ServiceB::class }
                provide { ServiceB() }
            }
}


class ScopedModuleA : Module() {
    override fun context() =
            declareContext {
                scope { ServiceA::class }
                provide { ServiceA(get()) }
            }
}


/**
 * Created by arnaud on 31/05/2017.
 */
class ScopeTest {

    @Test
    fun `get scoped instances`() {
        val ctx = Koin().build(ScopedModuleB())
        ctx.assertScopes(2)
        ctx.assertSizes(1, 0)
        Assert.assertNotNull(ctx.get<ServiceB>(ServiceB::class))

        Assert.assertEquals(1, ctx.instanceResolver.getInstanceFactory(Scope(ServiceB::class)).instances.size)
        ctx.assertSizes(1, 1)
    }

    @Test
    fun `can't get scoped instances`() {
        val ctx = Koin().build(ScopedModuleB())
        ctx.assertScopes(2)
        ctx.assertSizes(1, 0)
        Assert.assertNull(ctx.get<ServiceB>())

        Assert.assertEquals(0, ctx.instanceResolver.getInstanceFactory(Scope(ServiceB::class)).instances.size)
        ctx.assertSizes(1, 0)
    }

    @Test
    fun `get multi scoped instances`() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA())
        ctx.assertScopes(3)
        ctx.assertSizes(2, 0)
        Assert.assertNotNull(ctx.get<ServiceB>(ServiceB::class))
        Assert.assertNotNull(ctx.get<ServiceA>(ServiceA::class, ServiceB::class))

        Assert.assertEquals(1, ctx.instanceResolver.getInstanceFactory(Scope(ServiceB::class)).instances.size)
        Assert.assertEquals(1, ctx.instanceResolver.getInstanceFactory(Scope(ServiceA::class)).instances.size)
        ctx.assertSizes(2, 2)
    }
}