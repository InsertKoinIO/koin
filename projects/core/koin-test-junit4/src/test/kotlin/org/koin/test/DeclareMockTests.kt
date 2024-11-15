package org.koin.test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito

class DeclareMockTests : AutoCloseKoinTest() {

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun `declare and stub an existing definition`() {
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

        assertNotEquals(instance, mock)
        assertEquals(uuidValue, mock.getUUID())
    }

    @Test
    fun `declare and stub an existing scoped definition`() {
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

        assertNotEquals(instance, mock)
        assertEquals(uuidValue, mock.getUUID())
    }

    @Test
    fun `declare a Mock of an existing definition for given scope`() {
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

        assertNotEquals(instance, mock)
        assertEquals(uuidValue, mock.getUUID())
    }

    @Test
    fun `declare a Mock of an existing definition`() {
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

        assertNotEquals(instance, mock)
    }

    @Test
    fun `declare and mock an existing definition`() {
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

        assertNotEquals(instance, mock)
        assertEquals(uuidValue, mock.getUUID())
    }

    @Test
    fun `mock with qualifier`() {
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
