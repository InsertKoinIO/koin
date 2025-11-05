package org.koin.core

import co.touchlab.stately.concurrency.AtomicInt
import org.koin.Simple
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier._q
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetMultibindingTest {
    private val component1 = Simple.Component1()
    private val component2 = Simple.Component2()
    private val scopeId = "myScope"
    private val scopeKey = named("KEY")

    data class SetElement(
        val value: Int = 0
    ) {
        var name = ""
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
    fun `inject elements into set multibinding in root scope`() {
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
    fun `inject elements into set multibinding in none root scope`() {
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
    fun `override set multibinding elements in same module`() {
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
    }

    @Test
    fun `override set multibinding elements across modules`() {
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<SetElement> {
                        intoSet { SetElement().apply { name = "element1" } }
                    }
                },
                module {
                    declareSetMultibinding<SetElement> {
                        intoSet { SetElement().apply { name = "element2" } }
                    }
                },
            )
        }

        val koin = app.koin
        val rootSet: Set<SetElement> = koin.getSetMultibinding()
        assertEquals(1, rootSet.size)
        assertEquals("element2", (rootSet.first() as SetElement).name)
    }

    @Test
    fun `override set multibinding elements across modules and scopes`() {
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<SetElement> {
                        intoSet { SetElement().apply { name = "element1" } }
                    }
                },
                module {
                    scope(scopeKey) {
                        declareSetMultibinding<SetElement> {
                            intoSet { SetElement().apply { name = "element2" } }
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
        assertEquals("element1", (rootSet.first() as SetElement).name)
        assertEquals(1, scopeSet.size)
        assertEquals("element2", (scopeSet.first() as SetElement).name)
    }

    @Test
    fun `override set multibinding elements that define in linked scope`() {
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
    fun `declare set multibinding elements in separated modules`() {
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<Simple.ComponentInterface1> {
                        intoSet { component1 }
                    }
                    scope(scopeKey) {
                        declareSetMultibinding<Simple.ComponentInterface1> {
                            intoSet { component1 }
                        }
                    }
                },
                module {
                    declareSetMultibinding<Simple.ComponentInterface1> {
                        intoSet { component2 }
                    }
                    scope(scopeKey) {
                        declareSetMultibinding<Simple.ComponentInterface1> {
                            intoSet { component2 }
                        }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootSet: Set<Simple.ComponentInterface1> = koin.getSetMultibinding()
        val scopeSet: Set<Simple.ComponentInterface1> = myScope.getSetMultibinding()

        for (set in listOf(rootSet, scopeSet)) {
            assertEquals(2, set.size)
            assertTrue { set.containsAll(listOf(component1, component2)) }
        }
    }

    @Test
    fun `declare set multibinding elements through different MultibindingElementDefinitions`() {
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<Simple.ComponentInterface1> {
                        intoSet { component1 }
                    }
                    declareSetMultibinding<Simple.ComponentInterface1> {
                        intoSet { component2 }
                    }
                    scope(scopeKey) {
                        declareSetMultibinding<Simple.ComponentInterface1> {
                            intoSet { component1 }
                        }
                        declareSetMultibinding<Simple.ComponentInterface1> {
                            intoSet { component2 }
                        }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootSet: Set<Simple.ComponentInterface1> = koin.getSetMultibinding()
        val scopeSet: Set<Simple.ComponentInterface1> = myScope.getSetMultibinding()

        for (set in listOf(rootSet, scopeSet)) {
            assertEquals(2, set.size)
            assertTrue { set.containsAll(listOf(component1, component2)) }
        }
    }

    @Test
    fun `declare set multibinding elements by MultibindingElementDefinition reference`() {
        val app = koinApplication {
            modules(
                module {
                    val setElementDefinition =
                        declareSetMultibinding<Simple.ComponentInterface1>()
                    setElementDefinition.intoSet { component1 }
                    setElementDefinition.intoSet { component2 }
                    scope(scopeKey) {
                        val setElementDefinition =
                            declareSetMultibinding<Simple.ComponentInterface1>()
                        setElementDefinition.intoSet { component1 }
                        setElementDefinition.intoSet { component2 }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootSet: Set<Simple.ComponentInterface1> = koin.getSetMultibinding()
        val scopeSet: Set<Simple.ComponentInterface1> = myScope.getSetMultibinding()

        for (set in listOf(rootSet, scopeSet)) {
            assertEquals(2, set.size)
            assertTrue { set.containsAll(listOf(component1, component2)) }
        }
    }

    @Test
    fun `declare multibinding with specific qualifier`() {
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<Simple.ComponentInterface1>(_q("set1")) {
                        intoSet { component1 }
                        intoSet { component2 }
                    }
                    declareSetMultibinding<Simple.ComponentInterface1>(_q("set2")) {
                        intoSet { component1 }
                        intoSet { component2 }
                    }
                },
            )
        }

        val koin = app.koin
        val myScope = koin.createScope(scopeId, scopeKey)
        val rootSet: Set<Simple.ComponentInterface1> =
            koin.getSetMultibinding(_q("set1"))
        val scopeSet: Set<Simple.ComponentInterface1> =
            myScope.getSetMultibinding(_q("set2"))

        for (set in listOf(rootSet, scopeSet)) {
            assertEquals(2, set.size)
            assertTrue { set.containsAll(listOf(component1, component2)) }
        }
    }

    @Test
    fun `create set multibinding at start`() {
        val accumulator = AtomicInt(0)
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<Int>(createdAtStart = true) {
                        intoSet { accumulator.incrementAndGet() }
                        intoSet { accumulator.incrementAndGet() }
                    }
                },
            )
        }

        app.koin.getSetMultibinding<Int>()
        assertEquals(2, accumulator.get())
    }

    @Test
    fun `create set multibinding elements using parameters`() {
        val app = koinApplication {
            modules(
                module {
                    declareSetMultibinding<String> {
                        intoSet { it.get<String>() + "1" }
                        intoSet { it.get<String>() + "2" }
                    }
                },
            )
        }

        assertTrue {
            app.koin.getSetMultibinding<String> { parametersOf("set") }
                .containsAll(listOf("set1", "set2"))
        }
    }
}
