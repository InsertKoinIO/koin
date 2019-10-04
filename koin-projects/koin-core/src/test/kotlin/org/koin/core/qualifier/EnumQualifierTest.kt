package org.koin.core.qualifier

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class EnumQualifierTest {

    private enum class Indicate {
        LEFT, RIGHT
    }

    private enum class Turn {
        LEFT, RIGHT
    }

    @Test
    fun `qualified enums work correctly and are distinct`() {
        assertNotEquals(qualifier(Indicate.LEFT), qualifier(Indicate.RIGHT))
    }

    @Test
    fun `qualified enum not equal to string qualifier`() {
        assertNotEquals(qualifier(Indicate.LEFT).toString(), named("LEFT").toString())
    }

    @Test
    fun `qualified enum uses fully qualified name`() {
        assertEquals(qualifier(Indicate.LEFT).toString(), Indicate::class.java.name + ".LEFT")
    }

    @Test
    fun `different qualified enums with same name don't match`() {
        assertNotEquals(qualifier(Indicate.LEFT).toString(), qualifier(Turn.LEFT))
    }

    @Test
    fun `string and enum qualifiers can both be defined`() {
        try {
            startKoin {
                modules(
                    module {
                        single(named("RIGHT")) { "A" }
                        single(qualifier(Turn.RIGHT)) { "B" }
                    }
                )
            }
        } finally {
            stopKoin()
        }
    }

}
