package org.koin.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ext.closeScope
import org.koin.ext.getOrCreateScope
import org.koin.ext.scope

class ObjectScopeTest {

    @Test
    fun `play with scope`() {
        val koin = startKoin {
            modules(module {
                single { A() }
                scope<A> {
                    scoped { B() }
                    scoped { C() }
                }
            })
        }.koin

        val a = koin.get<A>()
        val b1 = koin.getOrNull<B>()
        assertNull(b1)
        assertNull(koin.getOrNull<C>())

        val scopeForA = a.getOrCreateScope()

        assertNotNull(scopeForA.get<B>())
        assertNotNull(scopeForA.get<C>())

        a.closeScope()

        assertNull(scopeForA.getOrNull<B>())
        assertNull(scopeForA.getOrNull<C>())

        val scopeForA2 = a.getOrCreateScope()

        val b2 = scopeForA2.getOrNull<B>()
        assertNotNull(b2)
        assertNotEquals(b1, b2)

        stopKoin()
    }

}