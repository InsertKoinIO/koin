package org.koin.test.koin

import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Koin
import org.koin.test.koin.example.MyClassNotTagged
import org.koin.test.koin.example.SampleModuleB


/**
 * Created by arnaud on 31/05/2017.
 */
class ScopeTest {

    @Test
    fun `don't inject into instance`() {
        val ctx = Koin().build(SampleModuleB::class)

        ctx.assertSizes(1, 0)

        val instance = MyClassNotTagged()

        val start = System.currentTimeMillis()

        ctx.inject(instance)

        val total = System.currentTimeMillis() - start
        println("inject in $total ms")

        ctx.assertSizes(1, 0)

        try {
            instance.serviceB
            fail()
        } catch(e: UninitializedPropertyAccessException) {
            assertNotNull(e)
        }
    }
}