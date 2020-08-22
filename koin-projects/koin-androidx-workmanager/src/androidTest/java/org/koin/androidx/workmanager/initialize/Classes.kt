package org.koin.androidx.workmanager.initialize

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * @author Fabio de Matos
 * @since 21/08/2020.
 */
class EchoService() {
    fun echoData(inputData: Data) = inputData
}

class EchoWorker(
    context: Context,
    parameters: WorkerParameters,
    private val echoService: EchoService
) :
    Worker(context, parameters) {
    override fun doWork(): Result {
        return when (inputData.size()) {
            0 -> Result.failure()
            else -> Result.success(echoService.echoData(inputData))
        }
    }
}

