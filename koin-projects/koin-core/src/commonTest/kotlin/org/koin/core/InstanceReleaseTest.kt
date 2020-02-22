package org.koin.core

import kotlin.test.assertEquals
import kotlin.test.Test
import org.koin.Simple
import org.koin.core.context.KoinContextHandler
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

class InstanceReleaseTest {

    @Test
    fun `can resolve a single`() {
        val module = module {
            single { (i: Int) -> Simple.MySingle(i) }
        }

        startKoin {
            modules(module)
        }

        val koin = KoinContextHandler.get()
        val a1 = koin.get<Simple.MySingle> { parametersOf(42) }
        assertEquals(42, a1.id)
        stopKoin()

        startKoin {
            modules(module)
        }

        val a3 = KoinContextHandler.get().get<Simple.MySingle> { parametersOf(24) }

        assertEquals(24, a3.id)

        stopKoin()
    }
}