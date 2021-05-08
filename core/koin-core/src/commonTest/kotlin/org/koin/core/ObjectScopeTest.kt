package org.koin.core

import kotlin.test.*
import kotlin.test.Test
import org.koin.Simple
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ObjectScopeTest {

    @AfterTest
    fun after(){
        stopKoin()
    }

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
    }

    @Test
    fun `typed scope & source`() {
        val koin = startKoin {
            modules(module {
                single { A() }
                scope<A> {
                    scoped { BofA(get()) }

                }
                scope<BofA> {
                    scoped { CofB(getSource()) }
                }
            })
        }.koin

        val a = koin.get<A>()
        val b = a.scope.get<BofA>()
        assertTrue(b.a == a)
        val c = b.scope.get<CofB>()
        assertTrue(c.b == b)
    }

    @Test
    fun `typed scope & source with get`() {
        val koin = startKoin {
            modules(module {
                single { A() }
                scope<A> {
                    scoped { BofA(get()) }

                }
                scope<BofA> {
                    scoped { CofB(get()) }
                }
            })
        }.koin

        val a = koin.get<A>()
        val b = a.scope.get<BofA>()
        assertTrue(b.a == a)
        val c = b.scope.get<CofB>()
        assertTrue(c.b == b)
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

        val scopeForA = koin.createScope(a)

        val b1 = scopeForA.get<B>()
        assertNotNull(b1)
        assertNotNull(scopeForA.get<C>())

        scopeForA.close()

        assertNull(scopeForA.getOrNull<B>())
        assertNull(scopeForA.getOrNull<C>())

        val scopeForA2 = koin.createScope(a)

        val b2 = scopeForA2.getOrNull<B>()
        assertNotNull(b2)
        assertNotEquals(b1, b2)
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

        a.scope.close()

        try {
            a.scope.get<B>()
        } catch (e: Exception) {

        }
    }

    @Test
    fun `scope property - koin isolation`() {
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
        var scope = koin.createScope(a)
        val b1 = scope.get<B>()
        scope.close()

        scope = koin.createScope(a)
        // recreate a new scope
        val b2 = scope.get<B>()

        assertNotEquals(b1, b2)
    }

    @Test
    fun `cascade scope `() {
        val koin = startKoin {
            modules(
                module {
                    factory { A() }
                    scope<A> {
                        scoped { B() }
                    }
                    scope<B> {
                        scoped { C() }
                    }
                })
        }.koin

        var a = koin.get<A>()
        val b1 = a.scope.get<B>()
        val c1 = b1.scope.get<C>()

        a.scope.close()
        b1.scope.close()

        // recreate a new scope
        a = koin.get()
        val b2 = a.scope.get<B>()
        val c2 = b2.scope.get<C>()

        assertNotEquals(b1, b2)
        assertNotEquals(c1, c2)
    }

    @Test
    fun `cascade linked scope `() {
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
        val b = a.scope.get<B>()
        a.scope.linkTo(b.scope)
        assertTrue(a.scope.get<C>() == b.scope.get<C>())
    }

    @Test
    fun `cascade unlink scope `() {
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
        a.scope.linkTo(b1.scope)
        val c1 = a.scope.get<C>()
        assertNotNull(c1)

        a.scope.unlink(b1.scope)
        assertNull(a.scope.getOrNull<C>())
    }

    @Test
    fun `shared linked scope `() {
        val koin: Koin = startKoin() {
            modules(
                module {
                    scope<A> {
                        scoped { Simple.ComponentB(get()) }
                    }
                    scope<B> {
                        scoped { Simple.ComponentB(get()) }
                    }
                    scope<C> {
                        scoped { Simple.ComponentA() }
                    }
                })
        }.koin


        val scopeA = koin.createScope<A>()
        val scopeB = koin.createScope<B>()
        val scopeC = koin.createScope<C>()
        scopeA.linkTo(scopeC)
        scopeB.linkTo(scopeC)

        val compb_scopeA = scopeA.get<Simple.ComponentB>()
        val compb_scopeB = scopeB.get<Simple.ComponentB>()

        assertNotEquals(compb_scopeA, compb_scopeB)
        //shared ComponentA instance
        assertEquals(compb_scopeA.a, compb_scopeB.a)
    }

    @OptIn(KoinInternalApi::class)
    @Test
    fun `error for root linked scope `() {
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
        try {
            koin.scopeRegistry.rootScope.linkTo(a.scope)
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}