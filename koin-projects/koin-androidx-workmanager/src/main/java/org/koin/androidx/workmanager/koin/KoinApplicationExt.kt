package org.koin.androidx.workmanager.koin

import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.KoinApplication
import org.koin.core.KoinExperimentalAPI
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Setup the KoinFragmentFactory instance
 */
@KoinExperimentalAPI
fun KoinApplication.setupWorkManagerFactory() {
    declareWorkerFactory()
    createWorkManagerFactory()
}

private val koinWorkerFactoryModule = module {
    single { KoinWorkerFactory() } bind WorkerFactory::class
}

private fun KoinApplication.declareWorkerFactory() {
    koin.loadModules(listOf(koinWorkerFactoryModule))
}

private fun KoinApplication.createWorkManagerFactory() {
    val factory = DelegatingWorkerFactory()
        .apply {
            addFactory(koin.get<KoinWorkerFactory>())
        }

    Configuration.Builder()
        .setWorkerFactory(factory)
        .build()
        .let { conf -> WorkManager.initialize(koin.get(), conf) }
}