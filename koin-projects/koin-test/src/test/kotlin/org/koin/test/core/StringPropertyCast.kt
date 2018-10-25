package org.koin.test.core

import org.junit.Test
import org.koin.ext.castValue

class StringPropertyCast {

    @Test
    fun `should have an Int`(){
        val t = "100".castValue()
        assert(t == 100)
    }

    @Test
    fun `should have a Float`(){
        val t = "100.0".castValue()
        assert(t == 100.0f)
    }
}