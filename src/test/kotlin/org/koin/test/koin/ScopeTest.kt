package org.koin.test.koin

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.dsl.context.Context
import org.koin.dsl.module.Module
import org.koin.test.ext.assertRootScopeSize
import org.koin.test.ext.assertScopeSize
import org.koin.test.ext.assertScopes
import org.koin.test.ext.assertSizes
import org.koin.test.koin.example.ServiceA
import org.koin.test.koin.example.ServiceB
import org.koin.test.koin.example.ServiceC


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

class NonScopedModuleC : Module() {
    override fun context(): Context = declareContext {
        provide { ServiceC(get(), get()) }
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

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertSizes(1, 1)
    }

    @Test
    fun `isolated scope - 1 instance`() {
        val ctx = Koin().build(ScopedModuleB())
        ctx.assertScopes(2)
        ctx.assertSizes(1, 0)
        Assert.assertNull(ctx.getOrNull<ServiceB>())

        ctx.assertScopeSize(ServiceB::class, 0)
        ctx.assertSizes(1, 0)
    }

    @Test
    fun `get multi scoped instances`() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA())
        ctx.assertScopes(3)
        ctx.assertSizes(2, 0)
        Assert.assertNotNull(ctx.get<ServiceB>(ServiceB::class))
        Assert.assertNotNull(ctx.get<ServiceA>(ServiceA::class, ServiceB::class))

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(2, 2)
    }

    @Test
    fun `get multi scoped instances with root`() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA(), NonScopedModuleC())
        ctx.assertScopes(3)
        ctx.assertSizes(3, 0)
        Assert.assertNotNull(ctx.get<ServiceC>(ServiceB::class, ServiceA::class))

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertRootScopeSize(1)
        ctx.assertSizes(3, 3)
    }

    @Test
    fun `isolated scope - 3 instance`() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA(), NonScopedModuleC())
        Assert.assertNotNull(ctx.get<ServiceC>(ServiceB::class, ServiceA::class))
        Assert.assertNull(ctx.getOrNull<ServiceB>(ServiceA::class))
        Assert.assertNull(ctx.getOrNull<ServiceA>(ServiceB::class))

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertRootScopeSize(1)
        ctx.assertSizes(3, 3)
    }

    @Test
    fun `scope release`() {
        val ctx = Koin().build(ScopedModuleB())
        ctx.assertScopes(2)
        ctx.assertSizes(1, 0)
        Assert.assertNotNull(ctx.get<ServiceB>(ServiceB::class))
        ctx.assertSizes(1, 1)
        ctx.assertScopeSize(ServiceB::class, 1)

        ctx.release(ServiceB::class)
        ctx.assertSizes(1, 0)
        ctx.assertScopeSize(ServiceB::class, 0)
    }
}