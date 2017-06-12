package org.koin.test.koin

import org.junit.Assert.*
import org.junit.Test
import org.koin.Koin
import org.koin.test.ServiceA
import org.koin.test.koin.example.SampleModuleB


/**
 * Created by arnaud on 31/05/2017.
 */
class StackTest {

    @Test
    fun simple_stack() {
        val ctx = Koin().build(SampleModuleB::class)

        ctx.stack { ServiceA(ctx.get()) }

        val serviceA_1: ServiceA = ctx.get()
        val serviceA_2: ServiceA? = ctx.getOrNull()

        assertNotNull(serviceA_1)
        assertNull(serviceA_2)
    }

    @Test
    fun chain_stack() {
        val ctx = Koin().build(SampleModuleB::class)

        ctx.stack { ServiceA(ctx.get()) }

        val serviceA_1: ServiceA = ctx.get()

        ctx.stack { ServiceA(ctx.get()) }

        val serviceA_2: ServiceA = ctx.get()

        val serviceA_3: ServiceA? = ctx.getOrNull()

        assertNotNull(serviceA_1)
        assertNotNull(serviceA_2)
        assertNull(serviceA_3)
        assertNotEquals(serviceA_1, serviceA_2)
    }
}