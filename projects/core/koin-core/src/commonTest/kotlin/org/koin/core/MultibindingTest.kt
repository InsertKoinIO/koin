package org.koin.core

import org.koin.Simple
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MultibindingTest {
    private val keyOfComponent1 = "component1"
    private val keyOfComponent2 = "component2"
    private val component1 = Simple.Component1()
    private val component2 = Simple.Component2()

    @Test
    fun `declare map multibinding in root scope`() {
        val app = koinApplication {
            modules(
                module {
                    declareMapMultibinding<String, Simple.ComponentInterface1>()
                },
            )
        }

        val koin = app.koin
        val map: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        val map2: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        assertEquals(map, map2)
        assertTrue { map.isEmpty() }
    }

    @Test
    fun `declare map multibinding in none root scope`() {
        val scopeId = "myScope"
        val scopeKey = named("KEY")
        val app = koinApplication {
            modules(
                module {
                    scope(scopeKey) {
                        declareMapMultibinding<String, Simple.ComponentInterface1>()
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val map: Map<String, Simple.ComponentInterface1> = myScope.getMapMultibinding()
        val map2: Map<String, Simple.ComponentInterface1> = myScope.getMapMultibinding()
        assertEquals(map, map2)
        assertTrue { map.isEmpty() }
    }

    @Test
    fun `inject some elements into map multibinding in root scope`() {
        val app = koinApplication {
            modules(
                module {
                    intoMap<String, Simple.ComponentInterface1>(keyOfComponent1) {
                        component1
                    }
                    intoMap<String, Simple.ComponentInterface1>(keyOfComponent2) {
                        component2
                    }
                },
            )
        }

        val koin = app.koin
        val map: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        val map2: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        assertEquals(map, map2)

        assertEquals(2, map.size)
        assertContains(map, keyOfComponent1)
        assertContains(map, keyOfComponent2)
        assertEquals(component1, map[keyOfComponent1])
        assertEquals(component2, map[keyOfComponent2])
    }

    @Test
    fun `inject some elements into map multibinding in none root scope`() {
        val scopeId = "myScope"
        val scopeKey = named("KEY")
        val app = koinApplication {
            modules(
                module {
                    scope(scopeKey) {
                        intoMap<String, Simple.ComponentInterface1>(keyOfComponent1) {
                            component1
                        }
                        intoMap<String, Simple.ComponentInterface1>(keyOfComponent2) {
                            component2
                        }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val map: Map<String, Simple.ComponentInterface1> = myScope.getMapMultibinding()
        val map2: Map<String, Simple.ComponentInterface1> = myScope.getMapMultibinding()
        assertEquals(map, map2)

        assertEquals(2, map.size)
        assertEquals(component1, map[keyOfComponent1])
        assertEquals(component2, map[keyOfComponent2])
    }

    @Test
    fun `map multibinding contains all elements that define in linked scope`() {
        val rootComponent1 = Simple.Component1()
        val rootComponent2 = Simple.Component2()
        val scopeComponent1 = Simple.Component1()
        val scopeComponent2 = Simple.Component2()
        val scopeId = "myScope"
        val scopeKey = named("KEY")
        val app = koinApplication {
            modules(
                module {
                    intoMap<String, Simple.ComponentInterface1>("rootComponent1") {
                        rootComponent1
                    }
                    intoMap<String, Simple.ComponentInterface1>("rootComponent2") {
                        rootComponent2
                    }
                    scope(scopeKey) {
                        intoMap<String, Simple.ComponentInterface1>("scopeComponent1") {
                            scopeComponent1
                        }
                        intoMap<String, Simple.ComponentInterface1>("scopeComponent2") {
                            scopeComponent2
                        }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootMap: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        val scopeMap: Map<String, Simple.ComponentInterface1> = myScope.getMapMultibinding()

        assertEquals(2, rootMap.size)
        assertEquals(rootComponent1, scopeMap["rootComponent1"])
        assertEquals(rootComponent2, scopeMap["rootComponent2"])
        assertEquals(4, scopeMap.size)
        assertEquals(rootComponent1, scopeMap["rootComponent1"])
        assertEquals(rootComponent2, scopeMap["rootComponent2"])
        assertEquals(scopeComponent1, scopeMap["scopeComponent1"])
        assertEquals(scopeComponent2, scopeMap["scopeComponent2"])
    }

    @Test
    fun `declare set multibinding in root scope`() {
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<Simple.ComponentInterface1>()
                },
            )
        }

        val koin = app.koin
        val set: Set<Simple.ComponentInterface1> = koin.getSetMultibinding()
        val set2: Set<Simple.ComponentInterface1> = koin.getSetMultibinding()
        assertEquals(set, set2)
        assertTrue { set.isEmpty() }
    }

    @Test
    fun `declare set multibinding in none root scope`() {
        val scopeId = "myScope"
        val scopeKey = named("KEY")
        val app = koinApplication {
            modules(
                module {
                    scope(scopeKey) {
                        declareSetMultibinding<Simple.ComponentInterface1>()
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val set: Set<Simple.ComponentInterface1> = myScope.getSetMultibinding()
        val set2: Set<Simple.ComponentInterface1> = myScope.getSetMultibinding()
        assertEquals(set, set2)
        assertTrue { set.isEmpty() }
    }

    @Test
    fun `inject some elements into set multibinding in root scope`() {
        val app = koinApplication {
            modules(
                module {
                    intoSet<Simple.ComponentInterface1> {
                        component1
                    }
                    intoSet<Simple.ComponentInterface1> {
                        component2
                    }
                },
            )
        }

        val koin = app.koin
        val set: Set<Simple.ComponentInterface1> = koin.getSetMultibinding()
        val set2: Set<Simple.ComponentInterface1> = koin.getSetMultibinding()
        assertEquals(set, set2)

        assertEquals(2, set.size)
        assertTrue { set.containsAll(listOf(component1, component2)) }
    }

    @Test
    fun `inject some elements into set multibinding in none root scope`() {
        val scopeId = "myScope"
        val scopeKey = named("KEY")
        val app = koinApplication {
            modules(
                module {
                    scope(scopeKey) {
                        intoSet<Simple.ComponentInterface1> {
                            component1
                        }
                        intoSet<Simple.ComponentInterface1> {
                            component2
                        }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val set: Set<Simple.ComponentInterface1> = myScope.getSetMultibinding()
        val set2: Set<Simple.ComponentInterface1> = myScope.getSetMultibinding()
        assertEquals(set, set2)

        assertEquals(2, set.size)
        assertTrue { set.containsAll(listOf(component1, component2)) }
    }

    @Test
    fun `set multibinding contains all elements that define in linked scope`() {
        val rootComponent1 = Simple.Component1()
        val rootComponent2 = Simple.Component2()
        val scopeComponent1 = Simple.Component1()
        val scopeComponent2 = Simple.Component2()
        val scopeId = "myScope"
        val scopeKey = named("KEY")
        val app = koinApplication {
            modules(
                module {
                    intoSet<Simple.ComponentInterface1> {
                        rootComponent1
                    }
                    intoSet<Simple.ComponentInterface1> {
                        rootComponent2
                    }
                    scope(scopeKey) {
                        intoSet<Simple.ComponentInterface1> {
                            scopeComponent1
                        }
                        intoSet<Simple.ComponentInterface1> {
                            scopeComponent2
                        }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootSet: Set<Simple.ComponentInterface1> = koin.getSetMultibinding()
        val scopeSet: Set<Simple.ComponentInterface1> = myScope.getSetMultibinding()

        assertEquals(2, rootSet.size)
        assertTrue { rootSet.containsAll(listOf(rootComponent1, rootComponent2)) }
        assertEquals(4, scopeSet.size)
        assertTrue { scopeSet.containsAll(listOf(rootComponent1, rootComponent2, scopeComponent1, scopeComponent2)) }
    }
}
