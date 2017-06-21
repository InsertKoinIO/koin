package org.koin.test.koin

import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.Koin
import org.koin.test.koin.example.MyInterface
import org.koin.test.koin.example.MyService

/**
 * Created by arnaud on 21/06/2017.
 */
class InterfaceBindingTest {

    @Test
    fun `bind component with its interface`() {
        val ctx = Koin().build()

        ctx.provide { MyService() }

        Assert.assertEquals(1, ctx.beanRegistry.definitions.size)
        Assert.assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotNull(ctx.get<MyInterface>())

        Assert.assertEquals(1, ctx.beanRegistry.definitions.size)
        Assert.assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `bind component defined by class with its interface`() {
        val ctx = Koin().build()

        ctx.provide(MyService::class)

        Assert.assertEquals(1, ctx.beanRegistry.definitions.size)
        Assert.assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotNull(ctx.get<MyInterface>())

        Assert.assertEquals(1, ctx.beanRegistry.definitions.size)
        Assert.assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)
    }
}