package org.koin.androidx.workmanager.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

/**
 * Provides an implementation of [WorkerFactory] that ties into Koin DI.
 *
 * It has access to an [WorkerParameters] object, and  creates a local scope with access to it.
 * Then such scope is used to instantiate an object of [ListenableWorker] through koin.
 *
 * @author : Fabio de Matos
 * @since : 16/02/2020
 **/
@OptIn(KoinApiExtension::class)
class KoinWorkerFactory : WorkerFactory(), KoinComponent {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return get(qualifier = named(workerClassName)) { parametersOf(workerParameters) }
    }
}

