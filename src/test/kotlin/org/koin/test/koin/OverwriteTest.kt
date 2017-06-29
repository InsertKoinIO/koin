package org.koin.test.koin

import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.Koin
import org.koin.test.ext.assertSizes
import org.koin.test.koin.example.SampleModuleB
import org.koin.test.koin.example.ServiceB
import org.mockito.Mockito.mock

/**
 * Created by arnaud on 31/05/2017.
 */
class OverwriteTest {

    @Test
    fun `overwrite an already existing bean definition`() {
        val ctx = Koin().build(SampleModuleB::class)

        ctx.assertSizes(1, 0)

        val serviceB = ctx.get<ServiceB>()

        ctx.assertSizes(1, 1)

        val mockB = mock(ServiceB::class.java)
        ctx.provide { mockB }

        val serviceBMock = ctx.get<ServiceB>()

        ctx.assertSizes(1, 1)

        assertNotEquals(serviceB, serviceBMock)
    }

}