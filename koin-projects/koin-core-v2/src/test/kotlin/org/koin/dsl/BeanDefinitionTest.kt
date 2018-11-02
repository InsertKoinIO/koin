package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple
import org.koin.core.bean.BeanDefinition

class BeanDefinitionTest {

    @Test
    fun `equals definitions`() {
        val def1 = BeanDefinition.createSingle(definition = { Simple.ComponentA() })
        val def2 = BeanDefinition.createSingle(definition = { Simple.ComponentA() })
        assertEquals(def1, def2)
    }

    @Test
    fun `equals definitions - but diff kind`() {
        val def1 = BeanDefinition.createSingle(definition = { Simple.ComponentA() })
        val def2 = BeanDefinition.createFactory(definition = { Simple.ComponentA() })
        assertEquals(def1, def2)
    }
}