package org.koin.test

import junit.framework.TestCase.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.dsl.module

class DeclareKoinContextFromRuleTest : AutoCloseKoinTest() {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger(Level.DEBUG)
        modules(module {
            single {
                Simple.ComponentA()
            }
            single {
                Simple.MyString("simple test")
            }
        })
    }

    private val componentA by inject<Simple.ComponentA>()

    @Test
    fun `component is injected to class variable`() {
        assertNotNull(componentA)
        assertEquals(Simple.ComponentA::class, componentA::class)
    }

    @Test
    fun whenMockWithQualifier() {
        val myString = koinTestRule.koin.get<Simple.MyString>()
        assertEquals(myString.s, "simple test")
    }
}
