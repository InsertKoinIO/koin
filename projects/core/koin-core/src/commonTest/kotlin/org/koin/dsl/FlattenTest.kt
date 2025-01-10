package org.koin.dsl

import org.koin.KoinCoreTest
import org.koin.core.module.flatten
import kotlin.test.Test
import kotlin.test.assertTrue

class FlattenTest : KoinCoreTest() {

    @Test
    fun test_simple_flatten() {
        val m1 = module { }
        val m2 = module { }
        val modules = m1 + m2

        assertTrue { flatten(modules) == setOf(m1, m2) }
    }

    @Test
    fun test_flatten_common() {
        val m1 = module { }
        val m2 = module { }
        val m3 = module { includes(m1) }
        val modules = m3 + m2

        assertTrue { flatten(modules) == setOf(m3, m1, m2) }
    }

    @Test
    fun test_flatten_common2() {
        val m1 = module { }
        val m2 = module { includes(m1) }
        val m3 = module { includes(m1) }
        val modules = m3 + m2

        assertTrue { flatten(modules) == setOf(m3, m1, m2) }
    }

    @Test
    fun test_flatten_level3() {
        val m4 = module { }
        val m1 = module { includes(m4) }
        val m2 = module { includes(m1) }
        val m3 = module { includes(m1) }
        val modules = m3 + m2

        assertTrue { flatten(modules) == setOf(m3, m1, m4, m2) }
    }
}
