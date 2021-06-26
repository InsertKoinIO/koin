package org.koin.core

import kotlin.test.assertEquals
import kotlin.test.Test
import org.koin.Simple
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

class InstanceReleaseTest {

    @Test
    fun `can resolve a single`() {
        val module = module {
            single { (i: Int) -> Simple.MyIntSingle(i) }
        }

        startKoin {
            modules(module)
        }

        val koin = KoinPlatformTools.defaultContext().get()
        val a1 = koin.get<Simple.MyIntSingle> { parametersOf(42) }
        assertEquals(42, a1.id)
        stopKoin()

        startKoin {
            modules(module)
        }

        val a3 = KoinPlatformTools.defaultContext().get().get<Simple.MyIntSingle> { parametersOf(24) }

        assertEquals(24, a3.id)

        stopKoin()
    }
}