package org.koin.test.core

import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.getKoin
import org.koin.test.KoinTest

class ScopeReleaseTest : KoinTest {

    class A
    class B

    @Test
    fun `release only scope instance`() {
        startKoin(listOf(module {
            single { A() }
            scope { B() }
        }))

        val koin = getKoin()

        val a_1 = get<A>()
        val b_1 = get<B>()

//        release("")
//
//        koin.getScope(koin.getRootModule()).close()
//        koin.getScope(Module.ROOT).close()
//
//
//        val a_2 = get<A>()
//        val b_2 = get<B>()
//
//        assertEquals(a_1, a_2)
//        assertNotEquals(b_1, b_2)
    }

}