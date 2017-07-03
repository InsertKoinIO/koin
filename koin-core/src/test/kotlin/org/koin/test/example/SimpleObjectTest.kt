package org.koin.test.example

import org.junit.Assert
import org.junit.Test

/**
 * Created by arnaud on 21/04/2017.
 */

class SimpleObjectTest {

    @Test
    fun `simple injection from Object container classes`() {

        val serviceA = Container.serviceA
        serviceA.doSomethingWithB()

        val serviceC = Container.serviceC
        serviceC.doSomethingWithAll()

        val serviceB = Container.serviceB
        Assert.assertNotNull(serviceA)
        Assert.assertNotNull(serviceB)
        Assert.assertNotNull(serviceC)

        Assert.assertEquals(serviceC.serviceA, serviceA)
        Assert.assertEquals(serviceC.serviceB, serviceB)
    }
}