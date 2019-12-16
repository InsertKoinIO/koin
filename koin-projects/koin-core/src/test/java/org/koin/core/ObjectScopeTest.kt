package org.koin.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.ext.closeScope
import org.koin.ext.createScope
import org.koin.ext.getOrCreateScope
import org.koin.ext.scope

class ObjectScopeTest {

    @Test
    fun `typed scope`() {
        val koin = koinApplication {
            modules(module {
                single { A() }
                scope<A> {
                    scoped { B() }
                    scoped { C() }
                }
            })
        }.koin

        assertNotNull(koin.get<A>())
        assertNull(koin.getOrNull<B>())
        assertNull(koin.getOrNull<C>())

        stopKoin()
    }

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

        val scopeForA = a.createScope()

        val b1 = scopeForA.get<B>()
        assertNotNull(b1)
        assertNotNull(scopeForA.get<C>())

        a.closeScope()

        assertNull(scopeForA.getOrNull<B>())
        assertNull(scopeForA.getOrNull<C>())

        val scopeForA2 = a.createScope()

        val b2 = scopeForA2.getOrNull<B>()
        assertNotNull(b2)
        assertNotEquals(b1, b2)

        stopKoin()
    }

    @Test
    fun `scope property`() {
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

        val b1 = a.scope.get<B>()
        assertNotNull(b1)
        assertNotNull(a.scope.get<C>())

        a.closeScope()

        val b2 = a.scope.get<B>()
        assertNotNull(b2)
        assertNotNull(a.scope.get<C>())
        assertNotEquals(b1, b2)

        stopKoin()
    }

    @Test
    fun `scope property 2`() {
        val koin = startKoin {
            modules(
                    module {
                        single { A() }
                        scope<A> {
                            scoped { B() }
                        }
                    })
        }.koin

        val a = koin.get<A>()

        // get current scope
        val b1 = a.scope.get<B>()

        a.closeScope()

        // recreate a new scope
        val b2 = a.scope.get<B>()

        assertNotEquals(b1, b2)

        stopKoin()
    }

    @Test
    fun `scope property - koin isolation`() {
        val koin = koinApplication {
            modules(
                    module {
                        single { A() }
                        scope<A> {
                            scoped { B() }
                        }
                    })
        }.koin

        val a = koin.get<A>()

        // get current scope
        val b1 = a.getOrCreateScope(koin).get<B>()

        a.closeScope(koin)

        // recreate a new scope
        val b2 = a.getOrCreateScope(koin).get<B>()

        assertNotEquals(b1, b2)
    }

    @Test
    fun `cascade scope `() {
        val koin = startKoin {
            modules(
                    module {
                        single { A() }
                        scope<A> {
                            scoped { B() }
                        }
                        scope<B> {
                            scoped { C() }
                        }
                    })
        }.koin

        val a = koin.get<A>()
        val b1 = a.scope.get<B>()
        val c1 = b1.scope.get<C>()

        a.closeScope()
        b1.closeScope()

        // recreate a new scope
        val b2 = a.scope.get<B>()
        val c2 = b2.scope.get<C>()

        assertNotEquals(b1, b2)
        assertNotEquals(c1, c2)

        stopKoin()
    }

}