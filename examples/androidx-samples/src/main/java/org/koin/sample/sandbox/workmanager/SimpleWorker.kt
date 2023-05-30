package org.koin.sample.sandbox.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters

/**
 * @author : Fabio de Matos
 * @since : 16/02/2020
 **/
class SimpleWorker(
    private val simpleWorkerService: SimpleWorkerService,
    appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        params
            .inputData
            .getInt(KEY_ANSWER, 0)
            .let {
                simpleWorkerService.addAnswer(it)
            }

        val d = Data.Builder()
            .putString("yes", "no")
            .build()

        return Result.success(d)
    }


    companion object {
        const val answer1st = 42
        const val answer2nd = 43
        const val KEY_ANSWER = "KEY_ANSWER"

        fun createData(answer: Int): Data {
            return Data.Builder()
                .putInt(KEY_ANSWER, answer)
                .build()
        }
    }
}