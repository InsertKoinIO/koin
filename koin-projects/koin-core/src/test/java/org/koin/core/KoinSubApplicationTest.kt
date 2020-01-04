package org.koin.core

import org.junit.Assert
import org.junit.Test
import org.koin.Simple
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope

class KoinSubApplicationTest {

    @Test
    fun `create koin application with parent`() {
        val parent = koinApplication {
            modules(
                    module {
                        single { Simple.ComponentA() }
                    })
        }.koin

        val koin = koinApplication(parent) {
            modules(
                    module {
                        single { Simple.ComponentB(a = get()) }
                    })
        }.koin

        val a1: Simple.ComponentA = parent.get()
        val a2: Simple.ComponentA = koin.get()
        val b: Simple.ComponentB = koin.get()

        Assert.assertEquals(a1, a2)
        Assert.assertNotNull(b)
        Assert.assertEquals(a1, b.a)
        Assert.assertEquals(a2, b.a)
    }

    @Test
    fun `sub application getAll`() {
        val parent = koinApplication {
            modules(
                    module {
                        single { Simple.ComponentA() }
                    })
        }.koin

        val koin = koinApplication(parent) {
            modules(
                    module {
                        single { Simple.ComponentB(a = get()) }
                    })
        }.koin

        val list1 = parent.getAll<Simple.ComponentA>()
        val list2 = koin.getAll<Simple.ComponentA>()

        Assert.assertEquals(list1.size,list2.size)
        Assert.assertEquals(list1[0], list2[0])
    }

    @Test
    fun `sub application scopes`() {
        val parent = koinApplication() {
            modules(
                    module {
                        single { A() }
                        scope<A> {
                            scoped { Simple.ComponentA() }
                        }
                    })
        }.koin

        val koin = koinApplication(parent) {
            modules(
                    module {
                        single { B() }
                        scope<B> {
                            scoped { Simple.ComponentB(a = get<A>().getOrCreateScope(this@koinApplication.koin).get()) }
                        }
                    })
        }.koin

        val a = koin.get<A>()
        val cA1 = a.getOrCreateScope(koin).get<Simple.ComponentA>()
        val cA2 = a.getOrCreateScope(koin).get<Simple.ComponentA>()

        val b = koin.get<B>()
        val cB1 = b.getOrCreateScope(koin).get<Simple.ComponentB>()
        val cB2 = b.getOrCreateScope(koin).get<Simple.ComponentB>()

        Assert.assertEquals(cA1, cA2)
        Assert.assertEquals(cB1, cB2)
        Assert.assertEquals(cA1, cB1.a)
        Assert.assertEquals(cA2, cB2.a)
    }
}