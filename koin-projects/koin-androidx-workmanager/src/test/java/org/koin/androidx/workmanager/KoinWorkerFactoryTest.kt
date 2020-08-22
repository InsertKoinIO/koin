package org.koin.androidx.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import org.junit.Assert.*
import org.junit.Test
import org.koin.androidx.workmanager.KoinWorkerFactory.Companion.getQualifier
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.mock


class KoinWorkerFactoryTest : AutoCloseKoinTest() {

    private val context = mock(Context::class.java)
    private val workerParams = mock(WorkerParameters::class.java)

    private fun ListenableWorker.getWorkerParameters(): WorkerParameters? {

        return this
            ?.let { it as? MyListenableWorker1 }
            ?.workerParams
    }

    @Test
    fun `instantiate worker`() {

        val koinApp = startKoin {
            printLogger(Level.DEBUG)
            modules(module {
                worker {
                    MyListenableWorker1(get(), get())
                }
            })
            workManagerFactory(context, init = null)
        }

        koinApp.koin
            .get<KoinWorkerFactory>()
            .createWorker(
                context,
                MyListenableWorker1::class.java.canonicalName ?: "",
                workerParams
            )
            .let {
                assertNotNull(it)
            }
    }

    @Test
    fun `Given call factory twice Should create different instances with different parameters`() {

        //////////////////////////////////
        // prepare test
        val context = mock(Context::class.java)
        val workerParams1 = mock(WorkerParameters::class.java)
        val workerParams2 = mock(WorkerParameters::class.java)


        val koinApp = startKoin {
            printLogger(Level.DEBUG)
            modules(module {
                worker { MyListenableWorker1(get(), get()) }
            })
            workManagerFactory(context, init = null)
        }

        //////////////////////////////////
        // run test
        val worker1 = koinApp.koin.get<KoinWorkerFactory>()
            .createWorker(
                context,
                getQualifier<MyListenableWorker1>().value,
                workerParams1
            )

        val worker2 = koinApp.koin.get<KoinWorkerFactory>()
            .createWorker(
                context,
                getQualifier<MyListenableWorker1>().value,
                workerParams2
            )

        //////////////////////////////////
        // check sanity of all objects
        assertNotNull(worker1)
        assertNotNull(worker1?.getWorkerParameters())
        assertEquals(workerParams1, worker1?.getWorkerParameters())

        assertNotNull(worker2)
        assertNotNull(worker2?.getWorkerParameters())
        assertEquals(workerParams2, worker2?.getWorkerParameters())

        //////////////////////////////////
        // verify objects are different
        assertNotEquals(worker1, worker2)
        assertNotEquals(worker1?.getWorkerParameters(), worker2?.getWorkerParameters())


    }

    @Test
    fun `Given context is declared with override Should instantiate object`() {

        val context = mock(Context::class.java)
        val workerParams = mock(WorkerParameters::class.java)

        val koinApp = startKoin {
            printLogger(Level.DEBUG)
            modules(module {
                factory(override = true) { context }
                worker { MyListenableWorker1(get(), get()) }
            })
            workManagerFactory(context, init = null)
        }

        koinApp.koin.get<KoinWorkerFactory>()
            .createWorker(
                context,
                MyListenableWorker1::class.java.canonicalName ?: "",
                workerParams
            )
            .let {
                assertNotNull(it)
            }
    }

    @Test
    fun `Given context is declared without override Should instantiate object`() {

        val context = mock(Context::class.java)
        val workerParams = mock(WorkerParameters::class.java)


        val koinApp = startKoin {
            printLogger(Level.DEBUG)
            modules(module {
                factory(override = false) { context }
                worker { MyListenableWorker1(get(), get()) }
            })
            workManagerFactory(context, init = null)
        }

        koinApp.koin.get<KoinWorkerFactory>()
            .createWorker(
                context,
                MyListenableWorker1::class.java.canonicalName ?: "",
                workerParams
            )
            .let {
                assertNotNull(it)
            }
    }

    @Test
    fun `Given 2 worker classes Should instantiate objects properly`() {

        //////////////////////////////////
        // prepare test
        val context = mock(Context::class.java)
        val workerParams1 = mock(WorkerParameters::class.java)
        val workerParams2 = mock(WorkerParameters::class.java)


        val koinApp = startKoin {
            printLogger(Level.DEBUG)
            modules(module {
                worker { MyListenableWorker1(get(), get()) }
                worker { MyListenableWorker2(get(), get(), get()) }
                single { DummyPayload("1") }

            })
            workManagerFactory(context, init = null)
        }

        //////////////////////////////////
        // run test
        val worker1 = koinApp.koin.get<KoinWorkerFactory>()
            .createWorker(
                context,
                getQualifier<MyListenableWorker1>().value,
                workerParams1
            )
                as? MyListenableWorker1

        val worker2 = koinApp.koin.get<KoinWorkerFactory>()
            .createWorker(
                context,
                getQualifier<MyListenableWorker2>().value,
                workerParams2
            )
                as? MyListenableWorker2

        //////////////////////////////////
        // check sanity of all objects
        assertNotNull(worker1)
        assertNotNull(worker1?.getWorkerParameters())
        assertEquals(workerParams1, worker1?.getWorkerParameters())

        assertNotNull(worker2)
        assertNotNull(worker2?.getWorkerParameters())
        assertEquals(workerParams2, worker2?.getWorkerParameters())


        //////////////////////////////////
        // verify objects are different
        assertNotEquals(worker1, worker2)
        assertNotEquals(worker1?.getWorkerParameters(), worker2?.getWorkerParameters())


    }

    @Test
    fun `Given standalone class Then qualifier must match`() {

        // this simulates how koin create the qualifier
        val qualifierByClass = getQualifier<MyListenableWorker1>()

        // this simulates how OS creates the qualifier through WorkerFactory::createWorker
        val qualifierByName =
            getQualifier(MyListenableWorker1::class.qualifiedName ?: "")

        assertEquals(qualifierByClass, qualifierByName)
    }

    @Test
    fun `Given inner class Then qualifier must match`() {

        // this simulates how koin create the qualifier
        val qualifierByClass = getQualifier<InnerWorkerClass>()

        // this simulates how OS creates the qualifier through WorkerFactory::createWorker
        val qualifierByName =
            getQualifier(InnerWorkerClass::class.java.name)

        assertEquals(qualifierByClass, qualifierByName)
    }

    @Test
    fun `problems with singleton`() {

        val context = mock(Context::class.java)
        val workerParams1 = mock(WorkerParameters::class.java)
        val workerParams2 = mock(WorkerParameters::class.java)

        val koinApp = startKoin {

            printLogger(Level.DEBUG)
            modules(module {

                worker { MyListenableWorker2(get(), get(), get()) }

                single { DummyPayload("1") }
            })
            workManagerFactory(context, init = null)
        }


        val worker1 = koinApp.koin.get<KoinWorkerFactory>()
            .createWorker(
                context,
                getQualifier<MyListenableWorker2>().value,
                workerParams1
            )
                as? MyListenableWorker2

        val worker2 = koinApp.koin.get<KoinWorkerFactory>()
            .createWorker(
                context,
                getQualifier<MyListenableWorker2>().value,
                workerParams2
            )
                as? MyListenableWorker2


        assertNotNull(worker1?.dummyPayload)
        assertNotNull(worker2?.dummyPayload)
        assertEquals(worker1?.dummyPayload, worker2?.dummyPayload)
    }


    class InnerWorkerClass(context: Context, workerParameters: WorkerParameters) :
        ListenableWorker(context, workerParameters) {
        override fun startWork(): ListenableFuture<Result> {
            TODO("Not yet implemented")
        }
    }
}