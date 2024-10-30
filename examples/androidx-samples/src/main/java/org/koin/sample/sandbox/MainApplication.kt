package org.koin.sample.sandbox

import android.app.Application
import androidx.work.WorkManager
import androidx.work.await
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.core.logger.Level
import org.koin.dsl.KoinAppDeclaration
import org.koin.sample.sandbox.di.allModules



class MainApplication : Application(), KoinStartup {

    override fun onKoinStartup() : KoinAppDeclaration = {
        androidLogger(Level.DEBUG)
        androidContext(this@MainApplication)
        androidFileProperties()
        fragmentFactory()
        workManagerFactory()
        modules(allModules)
    }

    companion object {
        var startTime: Long = 0
    }

    override fun onCreate() {
        super.onCreate()
        startTime = System.currentTimeMillis()
//        startKoin {
//            androidLogger(Level.DEBUG)
//            androidContext(this@MainApplication)
//            androidFileProperties()
//            fragmentFactory()
//            workManagerFactory()
//
//            modules(allModules)
//        }

        //TODO Load/Unload Koin modules scenario cases
        cancelPendingWorkManager(this)
    }
}

/**
 * If there is a pending work because of previous crash we'd like it to not run.
 *
 */
private fun cancelPendingWorkManager(mainApplication: MainApplication) {
    runBlocking {
        WorkManager.getInstance(mainApplication)
                .cancelAllWork()
                .result
                .await()
    }
}
