//package org.koin.androidx.workmanager
//
//import android.content.Context
//import androidx.work.ListenableWorker
//import androidx.work.WorkerParameters
//import com.google.common.util.concurrent.ListenableFuture
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertNotEquals
//import org.junit.Assert.assertNotNull
//import org.junit.Test
//import org.koin.android.ext.koin.androidContext
//import org.koin.androidx.workmanager.dsl.worker
//import org.koin.androidx.workmanager.factory.KoinWorkerFactory
//import org.koin.androidx.workmanager.koin.workManagerFactory
//import org.koin.core.context.startKoin
//import org.koin.core.logger.Level
//import org.koin.core.qualifier.named
//import org.koin.dsl.module
//import org.koin.test.AutoCloseKoinTest
//import org.mockito.Mockito.mock
//
//
//class KoinWorkerFactoryTest : AutoCloseKoinTest() {
//
//    private val context = mock(Context::class.java)
//    private val workerParams = mock(WorkerParameters::class.java)
//
//    private fun ListenableWorker.getWorkerParameters(): WorkerParameters? {
//        return this
//            .let { it as? MyListenableWorker1 }
//            ?.workerParams
//    }
//
//    @Test
//    fun `instantiate worker`() {
//
//        val koinApp = startKoin {
//            printLogger(Level.DEBUG)
//            modules(module {
//                worker {
//                    MyListenableWorker1(get(), get())
//                }
//            })
//            androidContext(context)
//            workManagerFactory()
//        }
//
//        koinApp.koin
//            .get<KoinWorkerFactory>()
//            .createWorker(
//                context,
//                MyListenableWorker1::class.java.canonicalName ?: "",
//                workerParams
//            )
//            .let {
//                assertNotNull(it)
//            }
//    }
//
//    @Test
//    fun `Given call factory twice Should create different instances with different parameters`() {
//
//        //////////////////////////////////
//        // prepare test
//        val context = mock(Context::class.java)
//        val workerParams1 = mock(WorkerParameters::class.java)
//        val workerParams2 = mock(WorkerParameters::class.java)
//
//
//        val koinApp = startKoin {
//            printLogger(Level.DEBUG)
//            modules(module {
//                worker { MyListenableWorker1(get(), get()) }
//            })
//            androidContext(context)
//            workManagerFactory()
//        }
//
//        //////////////////////////////////
//        // run test
//        val worker1 = koinApp.koin.get<KoinWorkerFactory>()
//            .createWorker(
//                context,
//                named<MyListenableWorker1>().value,
//                workerParams1
//            )
//
//        val worker2 = koinApp.koin.get<KoinWorkerFactory>()
//            .createWorker(
//                context,
//                named<MyListenableWorker1>().value,
//                workerParams2
//            )
//
//        //////////////////////////////////
//        // check sanity of all objects
//        assertNotNull(worker1)
//        assertNotNull(worker1.getWorkerParameters())
//        assertEquals(workerParams1, worker1.getWorkerParameters())
//
//        assertNotNull(worker2)
//        assertNotNull(worker2.getWorkerParameters())
//        assertEquals(workerParams2, worker2.getWorkerParameters())
//
//        //////////////////////////////////
//        // verify objects are different
//        assertNotEquals(worker1, worker2)
//        assertNotEquals(worker1.getWorkerParameters(), worker2.getWorkerParameters())
//
//
//    }
//
//    @Test
//    fun `Given context is declared with override Should instantiate object`() {
//
//        val context = mock(Context::class.java)
//        val workerParams = mock(WorkerParameters::class.java)
//
//        val koinApp = startKoin {
//            printLogger(Level.DEBUG)
//            modules(module {
//                factory(override = true) { context }
//                worker { MyListenableWorker1(get(), get()) }
//            })
//            androidContext(context)
//            workManagerFactory()
//        }
//
//        koinApp.koin.get<KoinWorkerFactory>()
//            .createWorker(
//                context,
//                MyListenableWorker1::class.java.canonicalName ?: "",
//                workerParams
//            )
//            .let {
//                assertNotNull(it)
//            }
//    }
//
//    @Test
//    fun `Given context is declared without override Should instantiate object`() {
//
//        val context = mock(Context::class.java)
//        val workerParams = mock(WorkerParameters::class.java)
//
//
//        val koinApp = startKoin {
//            printLogger(Level.DEBUG)
//            modules(module {
//                factory(override = false) { context }
//                worker { MyListenableWorker1(get(), get()) }
//            })
//            androidContext(context)
//            workManagerFactory()
//        }
//
//        koinApp.koin.get<KoinWorkerFactory>()
//            .createWorker(
//                context,
//                MyListenableWorker1::class.java.canonicalName ?: "",
//                workerParams
//            )
//            .let {
//                assertNotNull(it)
//            }
//    }
//
//    @Test
//    fun `Given 2 worker classes Should instantiate objects properly`() {
//
//        //////////////////////////////////
//        // prepare test
//        val context = mock(Context::class.java)
//        val workerParams1 = mock(WorkerParameters::class.java)
//        val workerParams2 = mock(WorkerParameters::class.java)
//
//
//        val koinApp = startKoin {
//            printLogger(Level.DEBUG)
//            modules(module {
//                worker { MyListenableWorker1(get(), get()) }
//                worker { MyListenableWorker2(get(), get(), get()) }
//                single { DummyPayload("1") }
//
//            })
//            androidContext(context)
//            workManagerFactory()
//        }
//
//        //////////////////////////////////
//        // run test
//        val worker1 = koinApp.koin.get<KoinWorkerFactory>()
//            .createWorker(
//                context,
//                named<MyListenableWorker1>().value,
//                workerParams1
//            )
//            as? MyListenableWorker1
//
//        val worker2 = koinApp.koin.get<KoinWorkerFactory>()
//            .createWorker(
//                context,
//                named<MyListenableWorker2>().value,
//                workerParams2
//            )
//            as? MyListenableWorker2
//
//        //////////////////////////////////
//        // check sanity of all objects
//        assertNotNull(worker1)
//        assertNotNull(worker1?.getWorkerParameters())
//        assertEquals(workerParams1, worker1?.getWorkerParameters())
//
//        assertNotNull(worker2)
//        assertNotNull(worker2?.getWorkerParameters())
//        assertEquals(workerParams2, worker2?.getWorkerParameters())
//
//
//        //////////////////////////////////
//        // verify objects are different
//        assertNotEquals(worker1, worker2)
//        assertNotEquals(worker1?.getWorkerParameters(), worker2?.getWorkerParameters())
//
//
//    }
//
//
//    @Test
//    fun `problems with singleton`() {
//
//        val context = mock(Context::class.java)
//        val workerParams1 = mock(WorkerParameters::class.java)
//        val workerParams2 = mock(WorkerParameters::class.java)
//
//        val koinApp = startKoin {
//
//            printLogger(Level.DEBUG)
//            modules(module {
//
//                worker { MyListenableWorker2(get(), get(), get()) }
//
//                single { DummyPayload("1") }
//            })
//            androidContext(context)
//            workManagerFactory()
//        }
//
//
//        val worker1 = koinApp.koin.get<KoinWorkerFactory>()
//            .createWorker(
//                context,
//                named<MyListenableWorker2>().value,
//                workerParams1
//            )
//            as? MyListenableWorker2
//
//        val worker2 = koinApp.koin.get<KoinWorkerFactory>()
//            .createWorker(
//                context,
//                named<MyListenableWorker2>().value,
//                workerParams2
//            )
//            as? MyListenableWorker2
//
//
//        assertNotNull(worker1?.dummyPayload)
//        assertNotNull(worker2?.dummyPayload)
//        assertEquals(worker1?.dummyPayload, worker2?.dummyPayload)
//    }
//
//
//    class InnerWorkerClass(context: Context, workerParameters: WorkerParameters) :
//        ListenableWorker(context, workerParameters) {
//        override fun startWork(): ListenableFuture<Result> {
//            TODO("Not yet implemented")
//        }
//    }
//}