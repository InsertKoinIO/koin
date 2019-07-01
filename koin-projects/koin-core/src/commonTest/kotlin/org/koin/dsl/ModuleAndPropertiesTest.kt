package org.koin.dsl

import org.koin.Simple
import org.koin.core.error.InstanceCreationException
import org.koin.core.mp.KoinMultiPlatform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ModuleAndPropertiesTest {

    @Test
    fun `get a property from a module`() {
        val key = "KEY"
        val value = "VALUE"
        val values = hashMapOf(key to value)

        val koin = koinApplication {
            properties(values)
            modules(module {
                single { Simple.MyStringFactory(getProperty(key)) }
            })
        }.koin

        val fact = koin.get<Simple.MyStringFactory>()
        assertEquals(value, fact.s)
    }

    @Test
    fun `missing property from a module`() {
        try {
            val key = "KEY"

            val koin = koinApplication {
                modules(module {
                    single { Simple.MyStringFactory(getProperty(key)) }
                })
            }.koin

            koin.get<Simple.MyStringFactory>()
            fail()
        } catch (e: InstanceCreationException) {
            KoinMultiPlatform.printStackTrace(e)
        }
    }
}
