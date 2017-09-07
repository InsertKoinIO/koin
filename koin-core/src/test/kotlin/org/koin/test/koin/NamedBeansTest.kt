package org.koin.test.koin

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.error.BeanDefinitionConflict
import org.koin.error.InstanceNotFoundException
import org.koin.test.ext.assertScopes
import org.koin.test.ext.assertSizes
import org.koin.test.koin.example.*

/**
 * Created by arnaud on 31/05/2017.
 */
class NamedBeansTest {

    @Test
    fun `get named bean instance`() {
        val ctx = Koin().build(MultiDeclareA())
        ctx.assertScopes(1)
        ctx.assertSizes(3, 0)
        Assert.assertNotNull(ctx.get<ServiceB>())

        val a1 = ctx.get<ServiceA>("A1")
        val a2 = ctx.get<ServiceA>("A2")

        Assert.assertNotEquals(a1, a2)

        ctx.assertSizes(3, 3)
    }

    @Test
    fun `error getting named bean instance`() {
        val ctx = Koin().build(MultiDeclareA())
        ctx.assertScopes(1)
        ctx.assertSizes(3, 0)
        Assert.assertNotNull(ctx.get<ServiceB>())

        try {
            ctx.get<ServiceA>()
            Assert.fail()
        } catch (e: BeanDefinitionConflict) {
        }
    }

    @Test
    fun `error getting module with conflicting named dependency`() {
        val ctx = Koin().build(ConflictingDependency())
        ctx.assertScopes(1)
        ctx.assertSizes(3, 0)

        try {
            ctx.get<ServiceB>()
            Assert.fail()
        } catch (e: BeanDefinitionConflict) {
        }
    }

    @Test
    fun `error with conflicting declaration `() {
        val ctx = Koin().build(ConflictingModule())
        ctx.assertScopes(1)
        ctx.assertSizes(2, 0)
        try {
            ctx.get<ServiceA>()
            Assert.fail()
        } catch (e: InstanceNotFoundException) {
        }
    }

    @Test
    fun `getting module with named dependency`() {
        val ctx = Koin().build(CleanMultiDependency())
        ctx.assertScopes(1)
        ctx.assertSizes(3, 0)

        val a = ctx.get<ServiceA>()
        Assert.assertNotNull(a)
        val b1 = ctx.get<ServiceB>("B1")
        Assert.assertNotNull(b1)
        Assert.assertNotNull(ctx.get<ServiceB>("B2"))

        Assert.assertEquals(a.serviceB, b1)

        ctx.assertSizes(3, 3)
    }

    @Test
    fun `provide for named dependency`() {
        val ctx = Koin().build(CleanMultiDependency())
        ctx.assertScopes(1)

        ctx.provide { ServiceC(ctx.get(), ctx.get("B1")) }

        ctx.assertSizes(4, 0)

        val a = ctx.get<ServiceA>()
        Assert.assertNotNull(a)
        val b1 = ctx.get<ServiceB>("B1")
        Assert.assertNotNull(b1)
        Assert.assertNotNull(ctx.get<ServiceB>("B2"))

        val c = ctx.get<ServiceC>()
        Assert.assertNotNull(c)
        Assert.assertEquals(c.serviceA, a)
        Assert.assertEquals(c.serviceB, b1)


        ctx.assertSizes(4, 4)
    }
}