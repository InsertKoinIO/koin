package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Test

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
                single("strings") { arrayListOf<String>() }
                single("ints") { arrayListOf<Int>() }
            })
        }.koin

        val strings = koin.get<ArrayList<String>>("strings")
        strings.add("test")

        assertEquals(1, koin.get<ArrayList<String>>("strings").size)

        assertEquals(0, koin.get<ArrayList<String>>("ints").size)
    }
}