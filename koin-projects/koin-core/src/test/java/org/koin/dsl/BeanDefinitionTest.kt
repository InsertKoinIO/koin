package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.Simple
import org.koin.core.definition.DefinitionFactory
import org.koin.core.definition.Kind
import org.koin.core.instance.InstanceContext
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.qualifier.named
import org.koin.test.getDefinition

class BeanDefinitionTest {

    val koin = koinApplication { }.koin

    @Test
    fun `equals definitions`() {

        val def1 = DefinitionFactory.createSingle(definition = { Simple.ComponentA() })
        val def2 = DefinitionFactory.createSingle(definition = { Simple.ComponentA() })
        assertEquals(def1, def2)
    }

    @Test
    fun `scope definition`() {
        val scopeID = named("scope")

        val def1 = DefinitionFactory.createSingle(scopeName = scopeID, definition = { Simple.ComponentA() })

        assertEquals(scopeID, def1.scopeName)
        assertEquals(Kind.Single, def1.kind)
        assertEquals(scopeID, def1.scopeName)
    }

    @Test
    fun `equals definitions - but diff kind`() {
        val def1 = DefinitionFactory.createSingle(definition = { Simple.ComponentA() })
        val def2 = DefinitionFactory.createFactory(definition = { Simple.ComponentA() })
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
        val name = named("A")
        val app = koinApplication {
            modules(
                    module {
                        single(name) { Simple.ComponentA() }
                        factory { Simple.ComponentB(get()) }
                    }
            )
        }

        val defA = app.getDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertEquals(name, defA.qualifier)

        val defB = app.getDefinition(Simple.ComponentB::class) ?: error("no definition found")
        assertTrue(defB.qualifier == null)
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
        val instance = defA.instance!!.get<Simple.ComponentA>(
                InstanceContext(
                        koin = app.koin,
                        _parameters = { emptyParametersHolder() })
        )
        assertEquals(instance, app.koin.get<Simple.ComponentA>())
    }
}