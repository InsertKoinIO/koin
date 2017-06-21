package org.koin.test.koin

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.Koin
import org.koin.test.ServiceB
import org.koin.test.koin.example.MyClass
import org.koin.test.koin.example.MyOtherClass
import org.koin.test.koin.example.SampleModuleAC
import org.koin.test.koin.example.SampleModuleB
import org.mockito.Mockito


/**
 * Created by arnaud on 31/05/2017.
 */
class InjectTest {

    @Test
    fun `inject into tagged instance`() {
        val ctx = Koin().build(SampleModuleB::class)

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val instance = MyClass()

        val start = System.currentTimeMillis()

        ctx.inject(instance)

        val total = System.currentTimeMillis() - start
        println("inject in $total ms")

        assertEquals(1, ctx.beanRegistry.definitions.size)
        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)

        assertNotNull(instance.serviceB)
        instance.serviceB.doSomething()
    }

    @Test
    fun `inject multiple components into instance`() {
        val ctx = Koin().build(SampleModuleAC::class)

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val instance = MyOtherClass()

        val start = System.currentTimeMillis()

        ctx.inject(instance)

        val total = System.currentTimeMillis() - start
        println("inject in $total ms")

        assertNotNull(instance.serviceB)
        assertNotNull(instance.serviceC)

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(3, ctx.beanRegistry.instanceFactory.instances.size)
    }

    /*
     To compare with @inject
     */
    @Test
    fun `manual binding into instance`() {
        val ctx = Koin().build(SampleModuleAC::class)

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

        val instance = MyOtherClass()

        val start = System.currentTimeMillis()

        instance.serviceB = ctx.get()
        instance.serviceC = ctx.get()

        val total = System.currentTimeMillis() - start
        println("inject in $total ms")

        assertNotNull(instance.serviceB)
        assertNotNull(instance.serviceC)

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(3, ctx.beanRegistry.instanceFactory.instances.size)
    }

    @Test
    fun `multi components inject with mocks`() {
        val ctx = Koin().build(SampleModuleAC::class)

        val serviceB: ServiceB = Mockito.mock(ServiceB::class.java)
        Mockito.`when`(serviceB.doSomething()).then {
            println("done B Mock")
        }
        ctx.provide { serviceB }

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)

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

        assertEquals(3, ctx.beanRegistry.definitions.size)
        assertEquals(3, ctx.beanRegistry.instanceFactory.instances.size)
    }
}