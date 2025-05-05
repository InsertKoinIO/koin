package org.koin.test.junit5

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.Simple
import org.koin.test.junit5.mock.MockProviderExtension
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito

class DeclareMockTests : AutoCloseKoinTest() {

    @JvmField
    @RegisterExtension
    val mockProvider = MockProviderExtension.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    @DisplayName("declare and stub an existing definition")
    fun whenStubbingExistingDefinition() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { Simple.UUIDComponent() }
                },
            )
        }.koin

        val instance = koin.get<Simple.UUIDComponent>()
        val uuidValue = "UUID"

        koin.declareMock<Simple.UUIDComponent> {
            given(getUUID()).will { uuidValue }
        }

        val mock = koin.get<Simple.UUIDComponent>()

        Assertions.assertNotEquals(instance, mock)
        Assertions.assertEquals(uuidValue, mock.getUUID())
    }

    @Test
    @DisplayName("declare and stub an existing scoped definition")
    fun whenStubbingExistingScopedDefinition() {
        val koin = koinApplication {
            modules(
                module {
                    scope(named<Simple>()) {
                        scoped { Simple.UUIDComponent() }
                    }
                },
            )
        }.koin

        val scope = koin.getOrCreateScope("scope_id", named<Simple>())

        val instance = scope.get<Simple.UUIDComponent>()
        val uuidValue = "UUID"

        scope.declareMock<Simple.UUIDComponent> {
            given(getUUID()).will { uuidValue }
        }

        val mock = scope.get<Simple.UUIDComponent>()

        Assertions.assertNotEquals(instance, mock)
        Assertions.assertEquals(uuidValue, mock.getUUID())
    }

    @Test
    @DisplayName("declare a Mock of an existing definition for given scope")
    fun whenDeclaringMockForExistingDefinitionForGivenScope() {
        val koin = koinApplication {
            modules(
                module {
                    scope(named<Simple>()) {
                        scoped { Simple.ComponentA() }
                    }
                },
            )
        }.koin

        val scope = koin.getOrCreateScope("scope_id", named<Simple>())

        val instance = scope.get<Simple.ComponentA>()

        scope.declareMock<Simple.ComponentA>()

        val mock = scope.get<Simple.ComponentA>()

        Assertions.assertNotEquals(instance, mock)
    }

    @Test
    @DisplayName("declare and mock an existing definition for given scope")
    fun whenDeclaringAndMockingAnExistingDefinitionForGivenScope() {
        val koin = koinApplication {
            modules(
                module {
                    scope(named<Simple>()) {
                        scoped { Simple.UUIDComponent() }
                    }
                },
            )
        }.koin

        val scope = koin.getOrCreateScope("scope_id", named<Simple>())

        val instance = scope.get<Simple.UUIDComponent>()
        val uuidValue = "UUID"

        scope.declareMock<Simple.UUIDComponent> {
            given(getUUID()).will { uuidValue }
        }

        val mock = scope.get<Simple.UUIDComponent>()

        Assertions.assertNotEquals(instance, mock)
        Assertions.assertEquals(uuidValue, mock.getUUID())
    }

    @Test
    @DisplayName("declare a Mock of an existing definition")
    fun whenDecralingMockOfAnExistingDefinition() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { Simple.ComponentA() }
                },
            )
        }.koin

        val instance = koin.get<Simple.ComponentA>()

        koin.declareMock<Simple.ComponentA>()

        val mock = koin.get<Simple.ComponentA>()

        Assertions.assertNotEquals(instance, mock)
    }

    @Test
    @DisplayName("declare and mock an existing definition")
    fun wheDeclaringAndMockingExistingDefinition() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { Simple.UUIDComponent() }
                },
            )
        }.koin

        val instance = koin.get<Simple.UUIDComponent>()
        val uuidValue = "UUID"

        koin.declareMock<Simple.UUIDComponent> {
            given(getUUID()).will { uuidValue }
        }

        val mock = koin.get<Simple.UUIDComponent>()

        Assertions.assertNotEquals(instance, mock)
        Assertions.assertEquals(uuidValue, mock.getUUID())
    }

    @Test
    @DisplayName("mock with qualifier")
    fun whenMockWithQualifier() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single(named("test")) { Simple.ComponentA() }
                },
            )
        }.koin

        koin.declareMock<Simple.ComponentA>(named("test"))
    }
}
