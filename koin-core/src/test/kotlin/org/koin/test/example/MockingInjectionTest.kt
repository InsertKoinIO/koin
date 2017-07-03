package org.koin.test.example

import org.junit.Test
import org.koin.test.koin.example.ServiceA
import org.koin.test.koin.example.ServiceB
import org.koin.test.koin.example.ServiceC
import org.mockito.Mockito.*

/**
 * Created by arnaud on 20/04/2017.
 */

class MockingInjectionTest {

    val serviceB: ServiceB = mock(ServiceB::class.java)
    val serviceA: ServiceA by lazy { ServiceA(serviceB) }

    @Test
    fun `simple mock injection`() {
        `when`(serviceB.process()).then {
            println("<mock> $this processor !")
        }
        serviceA.doSomethingWithB()
        verify(serviceB).process()
    }

    @Test
    fun `multiple mock injection`() {
        val serviceB: ServiceB = mock(ServiceB::class.java)
        val serviceA: ServiceA = mock(ServiceA::class.java)
        `when`(serviceA.doSomethingWithB()).then {
            println("<mock> A $this doSomethingWithB !")
            serviceB.process()
        }
        `when`(serviceB.process()).then {
            println("<mock> B $this processor !")
        }
        val serviceC = ServiceC(serviceA, serviceB)

        serviceC.doSomethingWithAll()
        verify(serviceA).doSomethingWithB()
        verify(serviceB, times(2)).process()
    }
}