package org.koin.test.koin

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

        ctx.assertSizes(1, 0)

        assertNotNull(ctx.get<MyInterface>())

        ctx.assertSizes(1, 1)
    }
}