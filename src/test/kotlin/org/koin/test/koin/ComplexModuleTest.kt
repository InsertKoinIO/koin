package org.koin.test.koin

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.test.ServiceA
import org.koin.test.ServiceB
import org.koin.test.koin.example.SampleModuleA

/**
 * Created by arnaud on 01/06/2017.
 */


class ComplexModuleTest {

    @Test
    fun module_with_previous_provide() {
        val ctx = Koin().build()

        val origin = ServiceB()
        ctx.provide { origin }

        ctx.import(SampleModuleA::class)

        val serviceB = ctx.get<ServiceB>()
        val serviceA = ctx.get<ServiceA>()

        Assert.assertEquals(origin, serviceB)
        Assert.assertNotNull(serviceB)
        Assert.assertNotNull(serviceA)
    }

}