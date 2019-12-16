package org.koin.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ext.closeScope
import org.koin.ext.getOrCreateScope

class ObjectScopeTest {

    @Test
    fun `scope from instance object`() {
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
        assertNull(koin.getOrNull<B>())
        assertNull(koin.getOrNull<C>())

        val scopeForA = a.getOrCreateScope()

        val b1 = scopeForA.get<B>()
        assertNotNull(b1)
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