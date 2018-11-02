package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple
import org.koin.core.module.createSingle

class BeanDefinitionTest {

    @Test
    fun `equals definitions`() {
        val def1 = createSingle(definition = { Simple.ComponentA() })
        val def2 = createSingle(definition = { Simple.ComponentA() })
        assertEquals(def1, def2)
    }
}