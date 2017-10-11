package org.koin.test

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.test.ext.assertContexts
import org.koin.test.ext.assertDefinedInScope
import org.koin.test.ext.assertDefinitions
import org.koin.test.ext.assertRemainingInstances

class ExternalDeclarationTest {

    class EmptyModule : Module() {
        override fun context() = applicationContext {
        }
    }

    class ComponentA

    @Test
    fun `should provide a component - external definition`() {
        val ctx = Koin().provide { ComponentA() }.build(EmptyModule())

        ctx.assertContexts(1)
        ctx.assertDefinitions(1)
        ctx.assertDefinedInScope(ComponentA::class, Scope.ROOT)

        Assert.assertNotNull(ctx.get<ComponentA>())
        ctx.assertRemainingInstances(1)
    }

    @Test
    fun `should provide a component - after definition`() {
        val ctx = Koin().build(EmptyModule())

        ctx.assertContexts(1)
        ctx.assertDefinitions(0)

        ctx.provide { ComponentA() }

        ctx.assertContexts(1)
        ctx.assertDefinitions(1)
        ctx.assertDefinedInScope(ComponentA::class, Scope.ROOT)

        Assert.assertNotNull(ctx.get<ComponentA>())
        ctx.assertRemainingInstances(1)
    }
}