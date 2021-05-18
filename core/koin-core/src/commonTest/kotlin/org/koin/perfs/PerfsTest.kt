package org.koin.perfs

import kotlin.test.Test
import org.koin.core.A
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

    @Test
    fun `perfModule400 module perfs`() {
        runPerfs()
        runPerfs()
    }

    private fun runPerfs() {
        val app = measureDurationForResult("perf400 - start ") {
            koinApplication {
                modules(perfModule400())
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

fun <T> measureDurationForResult(msg: String, code: () -> T): T {
    val result = measureDurationForResult(code)
    println("$msg in ${result.second} ms")
    return result.first
}