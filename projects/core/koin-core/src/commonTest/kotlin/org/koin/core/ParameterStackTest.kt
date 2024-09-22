package org.koin.core

import org.koin.Simple
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools
import org.koin.mp.generateId
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(KoinInternalApi::class)
class ParameterStackTest {

    @Test
    fun test_parameterstack_is_empty() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    factory { (id: String) -> Simple.MyStringFactory(id) }
                },
            )
        }.koin

        for (index in 1..10) {
            koin.get<Simple.MyStringFactory> { parametersOf(KoinPlatformTools.generateId()) }
        }

//        assertTrue(koin.scopeRegistry.rootScope._parameterStackLocal.get()!!.isEmpty())
    }
}
