package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.Simple
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.Kind
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.getScopeKey
import org.koin.test.getDefinition

class BeanDefinitionTest {

    @Test
    fun `equals definitions`() {
        val def1 = BeanDefinition.createSingle(definition = { Simple.ComponentA() })
        val def2 = BeanDefinition.createSingle(definition = { Simple.ComponentA() })
        assertEquals(def1, def2)
    }

    @Test
    fun `scope definition`() {
        val scopeID = "scope"

        val def1 = BeanDefinition.createScope(scopeKey = scopeID, definition = { Simple.ComponentA() })

        assertEquals(scopeID, def1.getScopeKey())
        assertEquals(Kind.Scope, def1.kind)
    }

    @Test
    fun `equals definitions - but diff kind`() {
        val def1 = BeanDefinition.createSingle(definition = { Simple.ComponentA() })
        val def2 = BeanDefinition.createFactory(definition = { Simple.ComponentA() })
        assertEquals(def1, def2)
    }

    @Test
    fun `definition kind`() {
        val app = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                    factory { Simple.ComponentB(get()) }
                }
            )
        }

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertEquals(Kind.Single, defA.kind)

        val defB = app.getDefinition(Simple.ComponentB::class) ?: error("no definition found")
        assertEquals(Kind.Factory, defB.kind)
    }

    @Test
    fun `definition name`() {
        val app = koinApplication {
            modules(
                module {
                    single("A") { Simple.ComponentA() }
                    factory { Simple.ComponentB(get()) }
                }
            )
        }

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertEquals("A", defA.name)

        val defB = app.getDefinition(Simple.ComponentB::class) ?: error("no definition found")
        assertTrue(defB.name == null)
    }

    @Test
    fun `definition function`() {
        val app = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                }
            )
        }

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        val instance = defA.instance.get<Simple.ComponentA>(null) { emptyParametersHolder() }
        assertEquals(instance, app.koin.get<Simple.ComponentA>())
    }
}