package org.koin.test.junit5

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.Koin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.Simple
import org.koin.test.inject

class DeclareKoinContextFromExtensionTest : KoinTest {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        printLogger(Level.DEBUG)
        modules(
            module {
                single {
                    Simple.ComponentA()
                }
                single {
                    Simple.MyString("simple test")
                }
            },
        )
    }

    private val componentA by inject<Simple.ComponentA>()

    @Test
    @DisplayName("component is injected to class variable")
    fun whenMockWithQualifier() {
        Assertions.assertNotNull(componentA)
        Assertions.assertEquals(Simple.ComponentA::class, componentA::class)
    }

    @Test
    @DisplayName("component can be fetched from koin context")
    fun whenMockWithQualifier(koin: Koin) {
        val myString = koin.get<Simple.MyString>()
        Assertions.assertEquals(myString.s, "simple test")
    }
}
