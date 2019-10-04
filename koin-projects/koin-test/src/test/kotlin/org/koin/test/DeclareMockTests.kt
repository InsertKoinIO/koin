package org.koin.test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceContext
import org.koin.core.logger.Level
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.registry.BeanRegistry
import org.koin.core.scope.RootScope
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.mock.createMockedDefinition
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class DeclareMockTests : KoinTest {

    @Test
    fun `create mock of an existing definition for given scope`() {

        val koin = koinApplication {
            modules(
                    module {
                        scope(named<Simple>()) {
                            scoped { Simple.ComponentA() }
                        }
                    }
            )
        }.koin

        val scope: Scope = koin.getOrCreateScope("scope_id", named<Simple>())

        val registry = scope.beanRegistry
        val definition = registry.findDefinitionTyped<RootScope, Simple.ComponentA>(null)
        val instance = scope.get<Simple.ComponentA>()

        val instanceFound = definition.instance?.get<Simple.ComponentA>(InstanceContext(scope) {emptyParametersHolder()})

        val mockDefinition = definition.createMockedDefinition()

        val mock = mockDefinition.instance?.get<Simple.ComponentA>(InstanceContext(scope) { emptyParametersHolder()})

        assertEquals(instance, instanceFound)
        assertNotEquals(instance, mock)

    }

    @Test
    fun `declare a Mock of an existing definition for given scope`() {
        val componentA = Simple.ComponentA()

        val koin = koinApplication {
            modules(
                    module {
                        scope(named<Simple>()) {
                            scoped { componentA }
                        }
                    }
            )
        }.koin

        val scope = koin.getOrCreateScope("scope_id", named<Simple>())

        val instance = scope.get<Simple.ComponentA>()

        scope.declareMock<Simple.ComponentA>()

        val mock = scope.get<Simple.ComponentA>()

        assertNotEquals(instance, mock)
    }

    @Test
    fun `declare and mock an existing definition for given scope`() {
        val koin = koinApplication {
            modules(
                    module {
                        scope(named<Simple>()) {
                            scoped { Simple.UUIDComponent() }
                        }
                    }
            )
        }.koin

        val scope = koin.getOrCreateScope("scope_id", named<Simple>())

        val instance = scope.get<Simple.UUIDComponent>()
        val uuidValue = "UUID"

        scope.declareMock<Simple.UUIDComponent> {
            Mockito.`when`(getUUID()).thenReturn(uuidValue)
        }

        val mock = scope.get<Simple.UUIDComponent>()

        assertNotEquals(instance, mock)
        assertEquals(uuidValue, mock.getUUID())

    }

    @Test
    fun `create mock of an existing definition`() {
        val koin = koinApplication {
            modules(
                    module {
                        single { Simple.ComponentA() }
                    }
            )
        }.koin

        val registry = koin.rootScope.beanRegistry
        val definition = registry.findDefinitionTyped<RootScope, Simple.ComponentA>(null)

        val mockedDefinition: BeanDefinition<*, Simple.ComponentA> = definition.createMockedDefinition()

        val rootScope = koin.rootScope
        val instance: Simple.ComponentA? = definition.instance?.get(
                InstanceContext(scope = rootScope, _parameters = { emptyParametersHolder() })
        )
        val mock = mockedDefinition.instance?.get<Simple.ComponentA>(
                InstanceContext(scope = rootScope, _parameters = { emptyParametersHolder() })
        )

        assertNotEquals(instance, mock)
    }

    @Test
    fun `declare a Mock of an existing definition`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        single { Simple.ComponentA() }
                    }
            )
        }.koin

        val instance = koin.get<Simple.ComponentA>()

        koin.declareMock<Simple.ComponentA>()

        val mock = koin.get<Simple.ComponentA>()

        assertNotEquals(instance, mock)
    }

    @Test
    fun `declare and mock an existing definition`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        single { Simple.UUIDComponent() }
                    }
            )
        }.koin

        val instance = koin.get<Simple.UUIDComponent>()
        val uuidValue = "UUID"

        koin.declareMock<Simple.UUIDComponent> {
            given(getUUID()).will { uuidValue }
        }

        val mock = koin.get<Simple.UUIDComponent>()

        assertNotEquals(instance, mock)
        assertEquals(uuidValue, mock.getUUID())
    }

    @Test
    fun `mock with qualifier`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(module {
                single(named("test")) { Simple.ComponentA() }
            })
        }.koin
        koin.declareMock<Simple.ComponentA>(named("test"))
    }

    @Test
    fun `can mock instance provided by object scope`() {
        val koin = koinApplication {
            modules(module {
                objectScope<Simple.UUIDComponent>()
            })
        }.koin

        val uuidValue = "UUID"
        val instance = Simple.UUIDComponent()
        val scope = koin.createObjectScoped(instance)
        assertEquals(instance, scope.get<Simple.UUIDComponent>())
        assertNotEquals(uuidValue, instance.getUUID())

        scope.declareMock<Simple.UUIDComponent> {
            given(getUUID()).will { uuidValue }
        }

        val mock = scope.get<Simple.UUIDComponent>()
        assertNotEquals(instance, mock)
        assertEquals(uuidValue, mock.getUUID())
    }
}

inline fun <S: Scope, reified T> BeanRegistry.findDefinitionTyped(
        qualifier: Qualifier? = null
): BeanDefinition<S, T> {
    return findDefinition(qualifier, T::class) as BeanDefinition<S, T>
}