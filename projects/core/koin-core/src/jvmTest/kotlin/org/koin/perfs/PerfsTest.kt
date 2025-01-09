package org.koin.perfs// package org.koin.perfs

 import org.koin.KoinCoreTest
 import kotlin.test.Test
 import org.koin.core.time.inMs
 import org.koin.dsl.koinApplication
 import org.koin.test.assertDefinitionsCount
 import kotlin.time.measureTime
 import kotlin.time.measureTimedValue

 class PerfsTest : KoinCoreTest(){

    @Test
    fun empty_module_perfs() {
        val res = measureTimedValue { koinApplication() }
        println("empty start in ${res.duration.inMs} ms")
        val app = res.value

        app.assertDefinitionsCount(0)
        app.close()
    }


    @Test
    fun perfModule400_module_perfs() {
        (1..3).forEach { runPerfs() }
    }

    private fun runPerfs() {
        val appMeasure = measureTimedValue {
            koinApplication {
                modules(perfModule400())
            }
        }
        println("perf400 - start in ${appMeasure.duration.inMs} ms")

        val koin = appMeasure.value.koin


        val runDuration = measureTime {
            koin.get<Perfs.A27>()
            koin.get<Perfs.B31>()
            koin.get<Perfs.C12>()
            koin.get<Perfs.D42>()
        }
        println("perf400 - executed in ${runDuration.inMs} ms")
    }
 }
