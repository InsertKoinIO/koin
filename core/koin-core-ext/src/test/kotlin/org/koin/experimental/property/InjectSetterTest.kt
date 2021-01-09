package org.koin.experimental.property

import kotlin.test.assertEquals
import kotlin.test.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class B
class C

class A {
    lateinit var b: B
    lateinit var c: C
}

class PlayTest {


    @Test
    fun `play setter injection`() {
        val koin = startKoin {
            modules(module {
                single { B() }
                single { C() }
            })
        }.koin

        val a = A()
        a.inject(a::b, a::c)

        assertEquals(koin.get<B>(),a.b)
        assertEquals(koin.get<C>(),a.c)

        stopKoin()
    }

}