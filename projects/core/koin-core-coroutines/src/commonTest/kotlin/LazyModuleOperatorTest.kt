package org.koin.test

import org.koin.dsl.lazyModule
import kotlin.test.Test
import kotlin.test.assertEquals

class LazyModuleOperatorTest {

    @Test
    fun module_config(){

    }

    @Test
    fun test_include() {
        val m2 = lazyModule {}
        val m1 = lazyModule {}
        val all = m1 + m2

        assertEquals(2,all.size)
    }
}
