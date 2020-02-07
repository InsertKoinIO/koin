package org.koin.androidx.workmanager.koin

import android.content.Context
import androidx.work.*
import org.koin.androidx.workmanager.KoinWorkerFactory
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.module
import kotlin.reflect.KFunction3

/**
 * Setup the KoinFragmentFactory instance
 */
fun KoinApplication.workManagerFactory(
    context: Context,
    init: KFunction3<Koin, Context, Array<out WorkerFactory>, Unit>? = ::setupWorkManagerFactory,
    vararg workerFactories: WorkerFactory
) {
    koin.loadModules(listOf(module {

        scope<KoinWorkerFactory> {

        }

        single<KoinWorkerFactory> {
            KoinWorkerFactory()
        }
    }))

    koin.createRootScope()

    init?.invoke(koin, context, workerFactories)

}


fun setupWorkManagerFactory(
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
                .build()
                .let { conf -> WorkManager.initialize(context, conf) }
        }
}