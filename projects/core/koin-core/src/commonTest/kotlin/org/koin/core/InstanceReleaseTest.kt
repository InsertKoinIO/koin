package org.koin.core

import org.koin.Simple
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools
import kotlin.test.Test
import kotlin.test.assertEquals

class InstanceReleaseTest {

    @Test
    fun `can resolve a single`() {
        val module = module {
            single { (i: Int) -> Simple.MySingle(i) }
        }

        startKoin {
            modules(module)
        }

        val koin = KoinPlatformTools.defaultContext().get()
        val a1 = koin.get<Simple.MySingle> { parametersOf(42) }
        assertEquals(42, a1.id)
        stopKoin()

        startKoin {
            modules(module)
        }

        val a3 = KoinPlatformTools.defaultContext().get().get<Simple.MySingle> { parametersOf(24) }

        assertEquals(24, a3.id)

        stopKoin()
    }
}
