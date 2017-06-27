package org.koin.test.koin

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.Koin
import org.koin.test.koin.example.SampleModuleD
import org.koin.test.koin.example.ServiceD

/**
 * Created by arnaud on 31/05/2017.
 */
class PropertyTest {

    @Test
    fun `property has been added`() {
        val ctx = Koin()
                .properties(mapOf("myVal" to "VALUE!"))
                .build(SampleModuleD::class)

        val myVal = ctx.getProperty<String>("myVal")
        assertNotNull(myVal)

        ctx.assertProps(1)

        val serviceD = ctx.get<ServiceD>()
        assertNotNull(serviceD)
    }

    @Test
    fun `set a property on context`() {
        val ctx = Koin().build(SampleModuleD::class)

        ctx.assertProps(0)

        val myVal = "myVal"
        ctx.setProperty("myVal", myVal)

        ctx.assertProps(1)

        val serviceD = ctx.get<ServiceD>()
        assertEquals(myVal, serviceD.myVal)
    }
}