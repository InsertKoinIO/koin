package org.koin.perfs

import kotlin.test.Test
import org.koin.core.A
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.Definitions
import org.koin.core.definition.Options
import org.koin.core.scope.ScopeDefinition
import org.koin.core.time.measureDurationForResult
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.assertDefinitionsCount

class PerfsTest {

    @Test
    fun `empty module perfs`() {
        val app = measureDurationForResult("empty - start ") {
            koinApplication()
        }

        app.assertDefinitionsCount(0)
        app.close()
    }

    @Test
    fun `module no dsl`() {
        koinApplication().close()

        (1..10).forEach {
            useDSL()
            dontUseDSL()
        }
    }

    @OptIn(KoinInternalApi::class)
    private fun dontUseDSL() {
        measureDurationForResult("no dsl ") {
            val app = koinApplication()
            app.koin.scopeRegistry.declareDefinition(
                Definitions.createSingle(
                    A::class,
                    definition = { A() },
                    options = Options(),
                    scopeQualifier = ScopeDefinition.ROOT_SCOPE_QUALIFIER)
            )
            app.close()
        }
    }

    private fun useDSL() {
        measureDurationForResult("dsl ") {
            val app = koinApplication {
                modules(module {
                    single { A() }
                })
            }
            app.close()
        }
    }

    /*
    Perfs on MBP 2018
        perf400 - start  - 136.426839 ms
        perf400 - executed - 0.95179 ms
        perf400 - start  - 0.480203 ms
        perf400 - executed - 0.034498 ms
     */
    @Test
    fun `perfModule400 module perfs`() {
        runPerfs()
        runPerfs()
    }

    private fun runPerfs() {
        val app = measureDurationForResult("perf400 - start ") {
            koinApplication {
                modules(perfModule400)
            }
        }
        val koin = app.koin

        measureDurationForResult("perf400 - executed") {
            koin.get<Perfs.A27>()
            koin.get<Perfs.B31>()
            koin.get<Perfs.C12>()
            koin.get<Perfs.D42>()
        }

        app.close()
    }
}