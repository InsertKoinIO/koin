package org.koin.sample.androidx.workmanager

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import junit.framework.Assert.assertEquals
import kotlinx.android.synthetic.main.workmanager_activity.*
import kotlinx.coroutines.*
import org.junit.Assert
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.lifecycleScope
import org.koin.core.logger.Level
import org.koin.sample.android.R
import org.koin.sample.androidx.mvp.MVPActivity
import org.koin.sample.androidx.utils.navigateTo

/**
 * @author : Fabio de Matos
 * @since : 16/02/2020
 **/
class WorkManagerActivity : AppCompatActivity() {

    private val worker: DummyService by inject()
    private val worker2: DummyService by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.workmanager_activity)
        title = "Android Work Manager"

        assertEquals(worker, worker2)

        CoroutineScope(Dispatchers.Default)
            .launch {

                val answer1 = try {
                    withTimeout(50_000) {

                        worker.popAnswer()
                            .let {
                                Assert.assertEquals(DummyWorker.answer1st, it)
                            }

                        worker.popAnswer()
                            .let {
                                Assert.assertEquals(DummyWorker.answer2nd, it)
                            }
                    }
                } catch (e: CancellationException) {
                    throw RuntimeException(e) // taking too long to receive 42 means WorkManager failed somehow
                }


                withContext(Dispatchers.Main) {
                    @SuppressLint("SetTextI18n")
                    workmanager_message.text = "Work Manager completed!"
                    workmanager_button.isEnabled = true
                }
            }


        CoroutineScope(Dispatchers.Default)
            .launch {

                WorkManager.getInstance(this@WorkManagerActivity)
                    .cancelAllWork()
                    .result
                    .await()

                // TODO find a way to make sure these run in the order expected,
                // TODO or implement it with something more complex than a shared channel for both workers.
                enqueueWork<DummyWorker>(DummyWorker.answer1st)
                enqueueWork<DummyWorker>(DummyWorker.answer2nd)
            }


        workmanager_button.setOnClickListener {
            navigateTo<MVPActivity>(isRoot = true)
        }
    }

    private inline fun <reified T : ListenableWorker> enqueueWork(answer: Int): OneTimeWorkRequest {


        return OneTimeWorkRequestBuilder<T>()
            .setInputData(DummyWorker.createData(answer))
            .build()
            .also {
                WorkManager.getInstance(this@WorkManagerActivity)
                    .enqueueUniqueWork(
                        DummyWorker::class.java.canonicalName?.toString() + answer
                            ?: "",
                        ExistingWorkPolicy.KEEP,
                        it
                    )
            }
    }
}