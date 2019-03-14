package org.koin.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class GenericDeclarationTest {

    val modules = module {
        single(named("strings")) { listOf("a string") }
        single(named("ints")) { listOf(42) }
    }

    @Test
    fun `declare and retrieve generic definitions`() {
        val koin = createKoin()

        val aString = koin.get<List<String>>(named("strings"))
        assertEquals("a string", aString[0])

        val anInt = koin.get<List<Int>>(named("ints"))
        assertEquals(42, anInt[0])
    }

    @Test
    fun `declare and not retrieve generic definitions`() {
        val koin = createKoin()

        try {
            koin.get<List<String>>()
            fail()
        } catch (e: NoBeanDefFoundException) {
            assertNotNull(e)
        }
    }

    private fun createKoin(): Koin {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(modules)
        }.koin
        return koin
    }
}