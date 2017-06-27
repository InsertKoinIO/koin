package org.koin.test.koin

import org.junit.Assert.*
import org.junit.Test
import org.koin.Koin
import org.koin.test.koin.example.*
import org.mockito.Mockito


/**
 * Created by arnaud on 31/05/2017.
 */
class InjectTest {

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

    @Test
    fun `don't inject all instances - miss tagged class`() {
        val ctx = Koin().build(SampleModuleC_ImportB::class)

        ctx.assertSizes(3, 0)

        val instance = MissedTagClass()

        val start = System.currentTimeMillis()

        ctx.inject(instance)

        val total = System.currentTimeMillis() - start
        println("inject in $total ms")

        ctx.assertSizes(3, 1)

        assertNotNull(instance.serviceB)

        try {
            instance.serviceC
            fail()
        } catch(e: UninitializedPropertyAccessException) {
            assertNotNull(e)
        }
    }

    @Test
    fun `inject into tagged instance`() {
        val ctx = Koin().build(SampleModuleB::class)

        ctx.assertSizes(1, 0)

        val instance = MyClass()

        val start = System.currentTimeMillis()

        ctx.inject(instance)

        val total = System.currentTimeMillis() - start
        println("inject in $total ms")

        ctx.assertSizes(1, 1)
        assertNotNull(instance.serviceB)
        instance.serviceB.doSomething()
    }

    @Test
    fun `inject multiple components into instance`() {
        val ctx = Koin().build(SampleModuleC_ImportB::class)

        ctx.assertSizes(3, 0)

        val instance = MyOtherClass()

        val start = System.currentTimeMillis()

        ctx.inject(instance)

        val total = System.currentTimeMillis() - start
        println("inject in $total ms")

        assertNotNull(instance.serviceB)
        assertNotNull(instance.serviceC)

        ctx.assertSizes(3, 3)
    }

    /*
     To compare with @inject
     */
    @Test
    fun `manual binding into instance`() {
        val ctx = Koin().build(SampleModuleC_ImportB::class)

        ctx.assertSizes(3, 0)

        val instance = MyOtherClass()

        val start = System.currentTimeMillis()

        instance.serviceB = ctx.get()
        instance.serviceC = ctx.get()

        val total = System.currentTimeMillis() - start
        println("inject in $total ms")

        assertNotNull(instance.serviceB)
        assertNotNull(instance.serviceC)

        ctx.assertSizes(3, 3)
    }

    @Test
    fun `multi components inject with mocks`() {
        val ctx = Koin().build(SampleModuleC_ImportB::class)

        val serviceB: ServiceB = Mockito.mock(ServiceB::class.java)
        Mockito.`when`(serviceB.doSomething()).then {
            println("done B Mock")
        }
        ctx.provide { serviceB }

        ctx.assertSizes(3, 0)

        val instance = MyOtherClass()

        val start = System.currentTimeMillis()

        ctx.inject(instance)

        val total = System.currentTimeMillis() - start
        println("inject in $total ms")

        assertNotNull(instance.serviceB)
        assertNotNull(instance.serviceC)

        instance.serviceB.doSomething()
        instance.serviceC.doSomethingWithAll()

        assertEquals(instance.serviceB, serviceB)

        Mockito.verify(serviceB, Mockito.times(3)).doSomething()

        ctx.assertSizes(3, 3)
    }
}