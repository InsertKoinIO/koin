// package org.koin.perfs
//
// import kotlin.test.Test
// import org.koin.core.annotation.KoinInternalApi
// import org.koin.core.logger.*
// import org.koin.dsl.koinApplication
// import org.koin.test.assertDefinitionsCount
// import kotlin.time.measureTime
// import kotlin.time.measureTimedValue
//
// class PerfsTest {
//
//    @Test
//    fun empty_module_perfs() {
//        val (app, duration) = measureTimedValue { koinApplication() }
//        println("empty start in $duration")
//        app.assertDefinitionsCount(0)
//        app.close()
//    }
//
//
//    @Test
//    fun perfModule400_module_perfs() {
//        (1..3).forEach { runPerfs() }
//    }
//
//    private fun runPerfs(log: Logger = EmptyLogger()) {
//        val (app, startKoinAppDuration) = measureTimedValue {
//            koinApplication {
//                modules(perfModule400())
//            }
//        }
//
//        println("perf400 - start in $startKoinAppDuration")
//
//        val koin = app.koin
//
//        val getDepsDuration = measureTime {
//            koin.get<Perfs.A27>()
//            koin.get<Perfs.B31>()
//            koin.get<Perfs.C12>()
//            koin.get<Perfs.D42>()
//        }
//
//        println("perf400 - executed in $getDepsDuration")
//
//        app.close()
//    }
// }
