package org.koin.androidx.workmanager

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.*
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androidx.workmanager.initialize.EchoService
import org.koin.androidx.workmanager.initialize.EchoWorker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.error.InstanceCreationException
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest

@RunWith(AndroidJUnit4::class)
class KoinApplicationExtTest : AutoCloseKoinTest() {

    private val context = InstrumentationRegistry.getInstrumentation().context

    private fun setupWorkManagerFactoryTest(
        koin: Koin,
        context: Context,
        workerFactories: Array<out WorkerFactory>
    ) {

        DelegatingWorkerFactory()
            .also { delegatingWorkerFactory ->
                // we add it first so it gets preference if a collision happens with [workerFactories]
                delegatingWorkerFactory.addFactory(koin.get<KoinWorkerFactory>())

                workerFactories
                    .forEach {
                        delegatingWorkerFactory.addFactory(it)
                    }
            }
            .also {
                Configuration.Builder()
                    .setWorkerFactory(it)
                    .setMinimumLoggingLevel(Log.DEBUG)
                    .setExecutor(SynchronousExecutor())
                    .build()
                    .let { conf ->
                        WorkManagerTestInitHelper.initializeTestWorkManager(context, conf)
                    }
            }
    }

    @Test(expected = InstanceCreationException::class)
    fun givenMissingDefinitionThenThrowExceptionTest() {

        val koinApp = startKoin {
            printLogger(Level.DEBUG)
            modules(module {
                factory { context }
                worker {
                    EchoWorker(get(), get(), get())
                }
            })
            workManagerFactory(
                context, init = ::setupWorkManagerFactoryTest
            )
        }

        // Create request
        val request = OneTimeWorkRequestBuilder<EchoWorker>()
            .build()

        WorkManager.getInstance(context)
            .enqueue(request)

    }

    @Test
    fun givenCorrectSetupThenRunWorkTest() {

        val koinApp = startKoin {
            printLogger(Level.DEBUG)
            modules(module {
                worker {
                    EchoWorker(get(), get(), get())
                }
                factory { EchoService() }
            })
            workManagerFactory(
                context, init = ::setupWorkManagerFactoryTest
            )

        }

        // Define input data
        val input = Data.Builder()
            .put("One", "1")
            .build()

        // Create request
        val request = OneTimeWorkRequestBuilder<EchoWorker>()
            .setInputData(input)
            .build()

        val workManager = WorkManager.getInstance(context)

        workManager.enqueue(request).result.get()


        val workInfo = workManager.getWorkInfoById(request.id).get()
        val outputData = workInfo.outputData

        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
        assertThat(outputData, `is`(input))
    }


    @Test
    fun givenCorrectSetupWithInnerClassThenRunWorkTest() {

        val koinApp = startKoin {
            printLogger(Level.DEBUG)
            modules(module {
                factory { context }
                worker {
                    InnerClassWorker(get(), get())
                }
            })
            workManagerFactory(
                context, init = ::setupWorkManagerFactoryTest
            )
        }

        // Create request
        val request = OneTimeWorkRequestBuilder<InnerClassWorker>()
            .build()

        WorkManager.getInstance(context)
            .enqueue(request)
            .result
            .get()
    }


    class InnerClassWorker(
        context: Context,
        parameters: WorkerParameters
    ) : Worker(context, parameters) {
        override fun doWork(): Result {
            return Result.success()
        }
    }

}


