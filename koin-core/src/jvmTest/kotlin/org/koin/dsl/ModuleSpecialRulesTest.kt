package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.qualifier.named

class ModuleSpecialRulesTest {

    @Test
    fun `generic type declaration`() {
        val koin = koinApplication {
            modules(module {
                single { arrayListOf<String>() }
            })
        }.koin

        koin.get<ArrayList<String>>()
    }

    @Test
    fun `generic types declaration`() {
        val koin = koinApplication {
            modules(module {
                single(named("strings")) { arrayListOf<String>() }
                single(named("ints")) { arrayListOf<Int>() }
            })
        }.koin

        val strings = koin.get<ArrayList<String>>(named("strings"))
        strings.add("test")

        assertEquals(1, koin.get<ArrayList<String>>(named("strings")).size)

        assertEquals(0, koin.get<ArrayList<String>>(named("ints")).size)
    }
}