package org.koin.core.qualifier

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.core.context.startKoin
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
        assertNotEquals(qualified(Indicate.LEFT), qualified(Indicate.RIGHT))
    }

    @Test
    fun `qualified enum not equal to string qualifier`() {
        assertNotEquals(qualified(Indicate.LEFT).toString(), named("LEFT").toString())
    }

    @Test
    fun `qualified enum uses fully qualified name`() {
        assertEquals(qualified(Indicate.LEFT).toString(), Indicate::class.java.name + ".LEFT")
    }

    @Test
    fun `different qualified enums with same name don't match`() {
        assertNotEquals(qualified(Indicate.LEFT).toString(), qualified(Turn.LEFT))
    }

    @Test
    fun `string and enum qualifiers can both be defined`() {
        startKoin {
            modules(
                module {
                    single(named("RIGHT")) { "A" }
                    single(qualified(Turn.RIGHT)) { "B" }
                }
            )
        }
    }

}
