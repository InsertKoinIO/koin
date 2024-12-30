package org.koin.core

import co.touchlab.stately.concurrency.AtomicInt
import org.koin.Simple
import org.koin.core.module.MapMultibindingKeyTypeException
import org.koin.core.parameter.parameterSetOf
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier._q
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MultibindingTest {
    private val keyOfComponent1 = "component1"
    private val keyOfComponent2 = "component2"
    private val component1 = Simple.Component1()
    private val component2 = Simple.Component2()
    private val scopeId = "myScope"
    private val scopeKey = named("KEY")

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
                    declareMapMultibinding<String, Simple.ComponentInterface1> {
                        intoMap(keyOfComponent1) { component1 }
                        intoMap(keyOfComponent2) { component2 }
                    }
                },
            )
        }

        val koin = app.koin
        val map: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        val map2: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        assertEquals(map, map2)

        assertEquals(2, map.size)
        assertContains(map, keyOfComponent2)
        assertContains(map, keyOfComponent2)
        assertEquals(component1, map[keyOfComponent1])
        assertEquals(component2, map[keyOfComponent2])
        assertNull(map["invalid key"])
    }

    @Test
    fun `inject some elements into map multibinding in none root scope`() {
        val app = koinApplication {
            modules(
                module {
                    scope(scopeKey) {
                        declareMapMultibinding<String, Simple.ComponentInterface1> {
                            intoMap(keyOfComponent1) { component1 }
                            intoMap(keyOfComponent2) { component2 }
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
        assertNull(map["invalid key"])
    }

    @Test
    fun `map multibinding contains all elements that define in linked scope`() {
        val rootComponent1 = Simple.Component1()
        val rootComponent2 = Simple.Component2()
        val scopeComponent1 = Simple.Component1()
        val scopeComponent2 = Simple.Component2()
        val app = koinApplication {
            modules(
                module {
                    declareMapMultibinding<String, Simple.ComponentInterface1> {
                        intoMap("rootComponent1") { rootComponent1 }
                        intoMap("rootComponent2") { rootComponent2 }
                    }
                    scope(scopeKey) {
                        declareMapMultibinding<String, Simple.ComponentInterface1> {
                            intoMap("scopeComponent1") { scopeComponent1 }
                            intoMap("scopeComponent2") { scopeComponent2 }
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
        assertNull(scopeMap["invalid key"])
        // in elements definition order
        assertEquals(
            listOf(
                rootComponent1,
                rootComponent2,
                scopeComponent1,
                scopeComponent2,
            ),
            scopeMap.values.toList()
        )
    }

    @Test
    fun `override map multibinding elements`() {
        val app = koinApplication {
            modules(
                module {
                    declareMapMultibinding<String, Simple.ComponentInterface1> {
                        intoMap(keyOfComponent1) { component1 }
                        intoMap(keyOfComponent1) { component2 }
                    }
                },
            )
        }

        val koin = app.koin
        val rootMap: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        assertEquals(1, rootMap.size)
        assertEquals(component2, rootMap[keyOfComponent1])
        koin.loadModules(
            listOf(
                module {
                    declareMapMultibinding<String, Simple.ComponentInterface1> {
                        intoMap(keyOfComponent1) { component1 }
                    }
                }
            )
        )
        assertEquals(1, rootMap.size)
        assertEquals(component1, rootMap[keyOfComponent1])
    }

    @Test
    fun `override map multibinding elements with equal keys but different toString`() {
        data class MapKey(val value: Int) {
            override fun toString(): String {
                return super<Any>.toString()
            }
        }

        val app = koinApplication {
            modules(
                module {
                    declareMapMultibinding<MapKey, Simple.ComponentInterface1> {
                        intoMap(MapKey(1)) { component1 }
                        intoMap(MapKey(1)) { component2 }
                    }
                },
            )
        }

        val koin = app.koin
        val rootMap: Map<MapKey, Simple.ComponentInterface1> = koin.getMapMultibinding()
        assertEquals(1, rootMap.size)
        assertEquals(component2, rootMap[MapKey(1)])
        assertNull(rootMap[MapKey(0)])
    }

    @Test
    fun `override map multibinding elements with different keys but same toString`() {
        data class MapKey(
            val name: String,
            val value: Int,
        ) {
            override fun toString(): String = name
        }

        val app = koinApplication {
            modules(
                module {
                    declareMapMultibinding<MapKey, Simple.ComponentInterface1> {
                        intoMap(MapKey(keyOfComponent1, 1)) { component1 }
                    }
                },
            )
        }
        val koin = app.koin
        val rootMap: Map<MapKey, Simple.ComponentInterface1> = koin.getMapMultibinding()
        assertEquals(1, rootMap.size)
        assertEquals(component1, rootMap[MapKey(keyOfComponent1, 1)])
        assertNull(rootMap[MapKey(keyOfComponent1, 2)])
        module {
            declareMapMultibinding<MapKey, Simple.ComponentInterface1> {
                intoMap(MapKey(keyOfComponent1, 1)) { component1 }
                assertFailsWith(MapMultibindingKeyTypeException::class) {
                    intoMap(MapKey(keyOfComponent1, 2)) { component1 }
                }
            }
        }
    }

    @Test
    fun `override map multibinding elements that define in linked scope`() {
        val rootComponent = Simple.Component1()
        val scopeComponent = Simple.Component2()
        val app = koinApplication {
            modules(
                module {
                    declareMapMultibinding<String, Simple.ComponentInterface1> {
                        intoMap(keyOfComponent1) { rootComponent }
                    }
                    scope(scopeKey) {
                        declareMapMultibinding<String, Simple.ComponentInterface1> {
                            intoMap(keyOfComponent1) { scopeComponent }
                            intoMap(keyOfComponent2) { Simple.Component2() }
                        }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootMap: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        val scopeMap: Map<String, Simple.ComponentInterface1> = myScope.getMapMultibinding()
        assertEquals(1, rootMap.size)
        assertEquals(rootComponent, rootMap[keyOfComponent1])
        assertTrue { rootMap.containsValue(rootComponent) }
        assertEquals(2, scopeMap.size)
        assertEquals(scopeComponent, scopeMap[keyOfComponent1])
        assertTrue { scopeMap.containsValue(scopeComponent) }
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
                    declareSetMultibinding<Simple.ComponentInterface1> {
                        intoSet { component1 }
                        intoSet { component2 }
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
        val app = koinApplication {
            modules(
                module {
                    scope(scopeKey) {
                        declareSetMultibinding<Simple.ComponentInterface1> {
                            intoSet { component1 }
                            intoSet { component2 }
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
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<Simple.ComponentInterface1> {
                        intoSet { rootComponent1 }
                        intoSet { rootComponent2 }
                    }
                    scope(scopeKey) {
                        declareSetMultibinding<Simple.ComponentInterface1> {
                            intoSet { scopeComponent1 }
                            intoSet { scopeComponent2 }
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
        assertTrue {
            scopeSet.containsAll(
                listOf(
                    rootComponent1,
                    rootComponent2,
                    scopeComponent1,
                    scopeComponent2,
                )
            )
        }
        // in elements definition order
        assertEquals(
            listOf(
                rootComponent1,
                rootComponent2,
                scopeComponent1,
                scopeComponent2,
            ),
            scopeSet.toList()
        )
    }

    @Test
    fun `override set multibinding elements`() {
        data class SetElement(
            val intValue: Int = 0
        ) {
            var name = ""
        }

        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<SetElement> {
                        intoSet { SetElement().apply { name = "element1" } }
                        intoSet { SetElement().apply { name = "element2" } }
                    }
                },
            )
        }

        val koin = app.koin
        val rootSet: Set<SetElement> = koin.getSetMultibinding()
        assertEquals(1, rootSet.size)
        assertEquals("element2", (rootSet.first() as SetElement).name)
        koin.loadModules(
            listOf(
                module {
                    declareSetMultibinding<SetElement> {
                        intoSet { SetElement().apply { name = "element1" } }
                    }
                }
            )
        )
        assertEquals(1, rootSet.size)
        assertEquals("element1", (rootSet.first() as SetElement).name)
    }

    @Test
    fun `override set multibinding elements that define in linked scope`() {
        data class SetElement(
            val intValue: Int = 0
        ) {
            var name = ""
        }

        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<SetElement> {
                        intoSet { SetElement().apply { name = "root" } }
                    }
                    scope(scopeKey) {
                        declareSetMultibinding<SetElement> {
                            intoSet { SetElement().apply { name = "scoped" } }
                        }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootSet: Set<SetElement> = koin.getSetMultibinding()
        val scopeSet: Set<SetElement> = myScope.getSetMultibinding()
        assertEquals(1, rootSet.size)
        assertEquals(1, scopeSet.size)
        assertEquals("root", (rootSet.first() as SetElement).name)
        assertEquals("scoped", (scopeSet.first() as SetElement).name)
    }

    @Test
    fun `declare multibinding elements in separated modules`() {
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<Simple.ComponentInterface1> {
                        intoSet { component1 }
                    }
                    declareMapMultibinding<String, Simple.ComponentInterface1> {
                        intoMap(keyOfComponent1) { component1 }
                    }
                    scope(scopeKey) {
                        declareSetMultibinding<Simple.ComponentInterface1> {
                            intoSet { component1 }
                        }
                        declareMapMultibinding<String, Simple.ComponentInterface1> {
                            intoMap(keyOfComponent1) { component1 }
                        }
                    }
                },
                module {
                    declareSetMultibinding<Simple.ComponentInterface1> {
                        intoSet { component2 }
                    }
                    declareMapMultibinding<String, Simple.ComponentInterface1> {
                        intoMap(keyOfComponent2) { component2 }
                    }
                    scope(scopeKey) {
                        declareSetMultibinding<Simple.ComponentInterface1> {
                            intoSet { component2 }
                        }
                        declareMapMultibinding<String, Simple.ComponentInterface1> {
                            intoMap(keyOfComponent2) { component2 }
                        }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootSet: Set<Simple.ComponentInterface1> = koin.getSetMultibinding()
        val scopeSet: Set<Simple.ComponentInterface1> = myScope.getSetMultibinding()
        val rootMap: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        val scopeMap: Map<String, Simple.ComponentInterface1> = myScope.getMapMultibinding()

        for (set in listOf(rootSet, scopeSet)) {
            assertEquals(2, set.size)
            assertTrue { set.containsAll(listOf(component1, component2)) }
        }
        for (map in listOf(rootMap, scopeMap)) {
            assertEquals(2, map.size)
            assertTrue { map.values.containsAll(listOf(component1, component2)) }
        }
    }

    @Test
    fun `declare multibinding elements through different MultibindingElementDefinitions`() {
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<Simple.ComponentInterface1> {
                        intoSet { component1 }
                    }
                    declareSetMultibinding<Simple.ComponentInterface1> {
                        intoSet { component2 }
                    }
                    declareMapMultibinding<String, Simple.ComponentInterface1> {
                        intoMap(keyOfComponent1) { component1 }
                    }
                    declareMapMultibinding<String, Simple.ComponentInterface1> {
                        intoMap(keyOfComponent2) { component2 }
                    }
                    scope(scopeKey) {
                        declareSetMultibinding<Simple.ComponentInterface1> {
                            intoSet { component1 }
                        }
                        declareSetMultibinding<Simple.ComponentInterface1> {
                            intoSet { component2 }
                        }
                        declareMapMultibinding<String, Simple.ComponentInterface1> {
                            intoMap(keyOfComponent1) { component1 }
                        }
                        declareMapMultibinding<String, Simple.ComponentInterface1> {
                            intoMap(keyOfComponent2) { component2 }
                        }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootSet: Set<Simple.ComponentInterface1> = koin.getSetMultibinding()
        val scopeSet: Set<Simple.ComponentInterface1> = myScope.getSetMultibinding()
        val rootMap: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        val scopeMap: Map<String, Simple.ComponentInterface1> = myScope.getMapMultibinding()

        for (set in listOf(rootSet, scopeSet)) {
            assertEquals(2, set.size)
            assertTrue { set.containsAll(listOf(component1, component2)) }
        }
        for (map in listOf(rootMap, scopeMap)) {
            assertEquals(2, map.size)
            assertTrue { map.values.containsAll(listOf(component1, component2)) }
        }
    }

    @Test
    fun `declare multibinding elements by MultibindingElementDefinition reference`() {
        val app = koinApplication {
            modules(
                module {
                    val setElementDefinition =
                        declareSetMultibinding<Simple.ComponentInterface1>()
                    setElementDefinition.intoSet { component1 }
                    setElementDefinition.intoSet { component2 }
                    val mapElementDefinition =
                        declareMapMultibinding<String, Simple.ComponentInterface1>()
                    mapElementDefinition.intoMap(keyOfComponent1) { component1 }
                    mapElementDefinition.intoMap(keyOfComponent2) { component2 }
                    scope(scopeKey) {
                        val setElementDefinition =
                            declareSetMultibinding<Simple.ComponentInterface1>()
                        setElementDefinition.intoSet { component1 }
                        setElementDefinition.intoSet { component2 }
                        val mapElementDefinition =
                            declareMapMultibinding<String, Simple.ComponentInterface1>()
                        mapElementDefinition.intoMap(keyOfComponent1) { component1 }
                        mapElementDefinition.intoMap(keyOfComponent2) { component2 }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootSet: Set<Simple.ComponentInterface1> = koin.getSetMultibinding()
        val scopeSet: Set<Simple.ComponentInterface1> = myScope.getSetMultibinding()
        val rootMap: Map<String, Simple.ComponentInterface1> = koin.getMapMultibinding()
        val scopeMap: Map<String, Simple.ComponentInterface1> = myScope.getMapMultibinding()

        for (set in listOf(rootSet, scopeSet)) {
            assertEquals(2, set.size)
            assertTrue { set.containsAll(listOf(component1, component2)) }
        }
        for (map in listOf(rootMap, scopeMap)) {
            assertEquals(2, map.size)
            assertTrue { map.values.containsAll(listOf(component1, component2)) }
        }
    }

    @Test
    fun `declare multibinding with specific qualifier`() {
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<Simple.ComponentInterface1>(_q<Set<*>>()) {
                        intoSet { component1 }
                        intoSet { component2 }
                    }
                    declareMapMultibinding<String, Simple.ComponentInterface1>(_q("map")) {
                        intoMap(keyOfComponent1) { component1 }
                        intoMap(keyOfComponent2) { component2 }
                    }
                    scope(scopeKey) {
                        declareSetMultibinding<Simple.ComponentInterface1>(_q<Set<*>>()) {
                            intoSet { component1 }
                            intoSet { component2 }
                        }
                        declareMapMultibinding<String, Simple.ComponentInterface1>(_q("map1")) {
                            intoMap(keyOfComponent1) { component1 }
                            intoMap(keyOfComponent2) { component2 }
                        }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootSet: Set<Simple.ComponentInterface1> =
            koin.getSetMultibinding(_q<Set<*>>())
        val scopeSet: Set<Simple.ComponentInterface1> =
            myScope.getSetMultibinding(_q<Set<*>>())
        val rootMap: Map<String, Simple.ComponentInterface1> =
            koin.getMapMultibinding(_q("map"))
        val scopeMap: Map<String, Simple.ComponentInterface1> =
            myScope.getMapMultibinding(_q("map1"))

        for (set in listOf(rootSet, scopeSet)) {
            assertEquals(2, set.size)
            assertTrue { set.containsAll(listOf(component1, component2)) }
        }
        for (map in listOf(rootMap, scopeMap)) {
            assertEquals(2, map.size)
            assertTrue { map.values.containsAll(listOf(component1, component2)) }
        }
    }

    @Test
    fun `create multibinding at start`() {
        val accumulator = AtomicInt(0)
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<Int>(createdAtStart = true) {
                        intoSet { accumulator.incrementAndGet() }
                        intoSet { accumulator.incrementAndGet() }
                    }
                    declareMapMultibinding<String, Int>(createdAtStart = true) {
                        intoMap("one") { accumulator.incrementAndGet() }
                        intoMap("two") { accumulator.incrementAndGet() }
                    }
                },
            )
        }

        val koin = app.koin
        koin.getSetMultibinding<Int>()
        koin.getMapMultibinding<String, Int>()
        assertEquals(4, accumulator.get())
    }

    @Test
    fun `create multibinding elements using parameters`() {
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<String> {
                        intoSet { it.get<String>() + "1" }
                        intoSet { it.get<String>() + "2" }
                    }
                    declareMapMultibinding<String, String> {
                        intoMap("one") { it.get<String>() + "1" }
                        intoMap("two") { it.get<String>() + "2" }
                    }
                },
            )
        }

        val koin = app.koin
        assertTrue {
            koin.getSetMultibinding<String> { parametersOf("set") }
                .containsAll(listOf("set1", "set2"))
        }
        assertTrue {
            koin.getMapMultibinding<String, String> { parametersOf("map") }
                .values.containsAll(listOf("map1", "map2"))
        }
    }
}
