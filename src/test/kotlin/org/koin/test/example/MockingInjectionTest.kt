package org.koin.test.example

import org.junit.Test
import org.koin.test.ServiceA
import org.koin.test.ServiceB
import org.koin.test.ServiceC
import org.mockito.Mockito.*

/**
 * Created by arnaud on 20/04/2017.
 */

class MockingInjectionTest {

    val serviceB: ServiceB = mock(ServiceB::class.java)
    val serviceA: ServiceA by lazy { ServiceA(serviceB) }

    @Test
    fun `simple mock injection`() {
        `when`(serviceB.doSomething()).then {
            println("<mock> $this doSomething !")
        }
        serviceA.doSomethingWithB()
        verify(serviceB).doSomething()
    }

    @Test
    fun `multiple mock injection`() {
        val serviceB: ServiceB = mock(ServiceB::class.java)
        val serviceA: ServiceA = mock(ServiceA::class.java)
        `when`(serviceA.doSomethingWithB()).then {
            println("<mock> A $this doSomethingWithB !")
            serviceB.doSomething()
        }
        `when`(serviceB.doSomething()).then {
            println("<mock> B $this doSomething !")
        }
        val serviceC = ServiceC(serviceA, serviceB)

        serviceC.doSomethingWithAll()
        verify(serviceA).doSomethingWithB()
        verify(serviceB, times(2)).doSomething()
    }
}