package org.koin.test.koin

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.Koin
import org.koin.test.ServiceB
import org.koin.test.koin.example.SampleModuleB
import org.mockito.Mockito.mock

/**
 * Created by arnaud on 31/05/2017.
 */
class KoinOverwriteTest {

    @Test
    fun overwrite_definition() {
        val ctx = Koin().build(SampleModuleB::class)

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val serviceB = ctx.get<ServiceB>()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        val mockB = mock(ServiceB::class.java)
        ctx.provide { mockB }

        val serviceBMock = ctx.get<ServiceB>()

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotEquals(serviceB, serviceBMock)
    }

}