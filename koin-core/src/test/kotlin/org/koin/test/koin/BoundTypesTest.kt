package org.koin.test.koin

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.test.ext.assertRootScopeSize
import org.koin.test.ext.assertScopes
import org.koin.test.ext.assertSizes
import org.koin.test.ext.getOrNull
import org.koin.test.koin.example.*

/**
 * Created by arnaud on 31/05/2017.
 */
class BoundTypesTest {

    @Test
    fun `get bounded instance`() {
        val ctx = Koin().build(BindModuleB())
        ctx.assertScopes(1)
        ctx.assertSizes(1, 0)
        Assert.assertNotNull(ctx.get<Processor>())

        ctx.assertRootScopeSize(1)
        ctx.assertSizes(1, 1)
    }

    @Test
    fun `should not get bounded instance`() {
        val ctx = Koin().build(SampleModuleB())
        ctx.assertScopes(1)
        ctx.assertSizes(1, 0)
        Assert.assertNull(ctx.getOrNull<Processor>())

        ctx.assertRootScopeSize(0)
        ctx.assertSizes(1, 0)
    }

    @Test
    fun `get same bounded instance`() {
        val ctx = Koin().build(BindModuleB())
        ctx.assertScopes(1)
        ctx.assertSizes(1, 0)
        val intf = ctx.get<Processor>()
        val servB = ctx.get<ServiceB>()

        Assert.assertEquals(servB, intf)
        ctx.assertRootScopeSize(1)
        ctx.assertSizes(1, 1)
    }

    @Test
    fun `inject with bounded instance`() {
        val ctx = Koin().build(SampleModuleOA(), BindModuleB())
        ctx.assertScopes(1)
        ctx.assertSizes(2, 0)

        val servA = ctx.get<OtherServiceA>()
        Assert.assertNotNull(servA)
        servA.doSomethingWithB()

        ctx.assertScopes(1)
        ctx.assertRootScopeSize(2)
        ctx.assertSizes(2, 2)
    }

    @Test
    fun `should not inject with bounded instance`() {
        val ctx = Koin().build(SampleModuleOA(), SampleModuleB())
        ctx.assertScopes(1)
        ctx.assertSizes(2, 0)

        Assert.assertNull(ctx.getOrNull<OtherServiceA>())

        ctx.assertScopes(1)
        ctx.assertRootScopeSize(0)
        ctx.assertSizes(2, 0)
    }

    @Test
    fun `should inject generic interface`() {
        val ctx = Koin().build(GenericProducerModule())
        ctx.assertScopes(1)
        ctx.assertSizes(1, 0)

        val producer = ctx.get<GenericProducer<String>>()
        Assert.assertEquals(ProducerImpl.OK, producer.produce())

        ctx.assertScopes(1)
        ctx.assertRootScopeSize(1)
        ctx.assertSizes(1, 1)
    }
}