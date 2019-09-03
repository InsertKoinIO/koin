package org.koin.core.qualifier

import org.junit.Assert
import org.junit.Test
import org.koin.core.qualifier.EnumQualifierTest.Way.LEFT
import org.koin.core.qualifier.EnumQualifierTest.Way.RIGHT

class EnumQualifierTest {

    private enum class Way {
        LEFT, RIGHT
    }

    @Test
    fun `named enum works correctly and are distinct`() {
        Assert.assertNotEquals(named(LEFT), named(RIGHT))
    }

}
