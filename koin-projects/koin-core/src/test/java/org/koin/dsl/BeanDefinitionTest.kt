package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.Simple
import org.koin.core.definition.Definitions
import org.koin.core.definition.Kind
import org.koin.core.definition.Options
import org.koin.core.instance.InstanceContext
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.qualifier.named
import org.koin.test.getBeanDefinition
import org.koin.test.getInstanceFactory

class BeanDefinitionTest {

    val koin = koinApplication { }.koin
    val rootScope = koin._scopeRegistry.rootScope

    @Test
    fun `equals definitions`() {

        val def1 = Definitions.createSingle(
            definition = { Simple.ComponentA() },
            scopeQualifier = rootScope._scopeDefinition.qualifier,
            options = Options()
        )
        val def2 = Definitions.createSingle(
            definition = { Simple.ComponentA() },
            scopeQualifier = rootScope._scopeDefinition.qualifier,
            options = Options()
        )
        assertEquals(def1, def2)
    }

    @Test
    fun `scope definition`() {
        val def1 = Definitions.createSingle(
            definition = { Simple.ComponentA() },
            scopeQualifier = rootScope._scopeDefinition.qualifier,
            options = Options()
        )

        assertEquals(rootScope._scopeDefinition.qualifier, def1.scopeQualifier)
        assertEquals(Kind.Single, def1.kind)
        assertEquals(rootScope._scopeDefinition.qualifier, def1.scopeQualifier)
    }

    @Test
    fun `equals definitions - but diff kind`() {
        val def1 = Definitions.createSingle(
            definition = { Simple.ComponentA() },
            scopeQualifier = rootScope._scopeDefinition.qualifier,
            options = Options()
        )
        val def2 = Definitions.createSingle(
            definition = { Simple.ComponentA() },
            scopeQualifier = rootScope._scopeDefinition.qualifier,
            options = Options()
        )
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

        val defA = app.getBeanDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertEquals(Kind.Single, defA.kind)

        val defB = app.getBeanDefinition(Simple.ComponentB::class) ?: error("no definition found")
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

        val defA = app.getBeanDefinition(Simple.ComponentA::class) ?: error("no definition found")
        assertEquals(name, defA.qualifier)

        val defB = app.getBeanDefinition(Simple.ComponentB::class) ?: error("no definition found")
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

        app.getBeanDefinition(Simple.ComponentA::class) ?: error("no definition found")
        val instance = app.getInstanceFactory(Simple.ComponentA::class)!!.get(
            InstanceContext(
                app.koin,
                rootScope,
                _parameters = { emptyParametersHolder() })
        )
        assertEquals(instance, app.koin.get<Simple.ComponentA>())
    }
}