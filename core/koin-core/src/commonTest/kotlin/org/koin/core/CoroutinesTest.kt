//package org.koin.core
//
//import org.koin.Simple
//import org.koin.core.context.startKoin
//import org.koin.core.context.stopKoin
//import org.koin.dsl.module
//import org.koin.test.getInstanceFactory
//import kotlin.random.Random
//import kotlin.test.Test
//
//class CoroutinesTest {
//
//    @Test
//    fun `KoinApp with coroutines gets`() = runBlocking {
//        val app = startKoin {
//            modules(
//                module {
//                    single { Simple.ComponentA() }
//                    single { Simple.ComponentB(get()) }
//                    single { Simple.ComponentC(get()) }
//                })
//        }
//        val koin = app.koin
//
//        val jobs = arrayListOf<Deferred<*>>()
//        jobs.add(async {
//            randomSleep()
//            koin.get<Simple.ComponentA>()
//        })
//        jobs.add(async {
//            randomSleep()
//            koin.get<Simple.ComponentB>()
//        })
//        jobs.add(async {
//            randomSleep()
//            koin.get<Simple.ComponentC>()
//        })
//        jobs.awaitAll()
//
//        val a = app.getInstanceFactory(Simple.ComponentA::class)!!
//        val b = app.getInstanceFactory(Simple.ComponentA::class)!!
//        val c = app.getInstanceFactory(Simple.ComponentA::class)!!
//
//        Assert.assertTrue(a.isCreated())
//        Assert.assertTrue(b.isCreated())
//        Assert.assertTrue(c.isCreated())
//
//        stopKoin()
//    }
//
//    private suspend fun randomSleep() {
//        val timer = Random.nextLong(MAX_TIME)
//        println("thread sleep  $timer")
//        delay(timer)
//    }
//}