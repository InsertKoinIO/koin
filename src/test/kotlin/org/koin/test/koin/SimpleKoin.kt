package org.koin.test.koin

import org.junit.Assert
import org.junit.Test
import org.koin.Koin
import org.koin.module.Module
import org.koin.test.koin.example.ServiceA
import org.koin.test.koin.example.ServiceB
import org.koin.test.koin.example.ServiceC

/**
 * Created by arnaud on 01/06/2017.
 */


class MyModule : Module() {
    override fun onLoad() {
        declareContext {
            provide { ServiceA(get()) }
            provide { ServiceB() }
            provide { ServiceC(get(), get()) }
        }
    }
}

class SimpleTest {

    @Test
    fun `simple load of MyModule`() {
        val ctx = Koin().build(MyModule::class)

        val serviceA = ctx.get<ServiceA>()
        serviceA.doSomethingWithB()

        val serviceC = ctx.get<ServiceC>()
        serviceC.doSomethingWithAll()

        val serviceB = ctx.get<ServiceB>()

        Assert.assertNotNull(serviceA)
        Assert.assertNotNull(serviceB)
        Assert.assertNotNull(serviceC)

        Assert.assertEquals(serviceC.serviceA, serviceA)
        Assert.assertEquals(serviceC.serviceB, serviceB)
    }

}