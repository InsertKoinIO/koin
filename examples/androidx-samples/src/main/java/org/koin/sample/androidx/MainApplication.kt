package org.koin.sample.androidx

import android.app.Application
import androidx.work.WorkManager
import androidx.work.await
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.sample.androidx.di.allModules

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            androidFileProperties()
            fragmentFactory()
            workManagerFactory()
            modules(allModules)
        }

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
