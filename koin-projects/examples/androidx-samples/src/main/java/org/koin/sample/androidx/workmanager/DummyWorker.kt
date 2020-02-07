package org.koin.sample.androidx.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters

/**
 * @author : Fabio de Matos
 * @since : 16/02/2020
 **/
class DummyWorker(
    private val dummyService: DummyService,
    appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val answer1st = 42
        const val answer2nd = 43

        private const val KEY_ANSWER = "KEY_ANSWER"

        fun createData(answer: Int): Data {
            return Data.Builder()
                .putInt(KEY_ANSWER, answer)
                .build()
        }
    }


    override suspend fun doWork(): Result {

        params
            .inputData
            .getInt(KEY_ANSWER, 0)
            .let {
                dummyService.addAnswer(it)
            }

        return Result.success()
    }

}