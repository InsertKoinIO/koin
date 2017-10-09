package org.koin.test.koin

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.test.ext.assertContexts
import org.koin.test.ext.assertDefinedInScope
import org.koin.test.ext.assertDefinitions


/**
 * Created by arnaud on 01/06/2017.
 */


class FlatInstancesModule : Module() {
    override fun context() =
            applicationContext {
                provide { CreateInstanceTest.ComponentA() }
                provide { CreateInstanceTest.ComponentB(get()) }
                provide { CreateInstanceTest.ComponentC(get(), get()) }
            }
}

class CreateInstanceTest {

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentB: ComponentB, val componentA: ComponentA)

    @Test
    fun `load and create instances`() {
        val ctx = Koin().build(FlatInstancesModule())

        val a = ctx.get<ComponentA>()
        val b = ctx.get<ComponentB>()
        val c = ctx.get<ComponentC>()

        Assert.assertNotNull(a)
        Assert.assertNotNull(c)
        Assert.assertNotNull(b)
        Assert.assertEquals(a, c.componentA)
        Assert.assertEquals(a, b.componentA)
        Assert.assertEquals(b, c.componentB)

        ctx.assertDefinitions(3)
        ctx.assertContexts(1)
        ctx.assertDefinedInScope(ComponentA::class, Scope.ROOT)
        ctx.assertDefinedInScope(ComponentB::class, Scope.ROOT)
        ctx.assertDefinedInScope(ComponentC::class, Scope.ROOT)
    }

}