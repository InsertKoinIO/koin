package org.koin.decompose

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.koin.compose.LocalKoinApplication
import org.koin.core.Koin
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Binds the [Context] to the dependency graph
 *
 * @author Antonio Vicente
 */
@Composable
actual fun AppContextBinder() {
    LocalKoinApplication.current.androidContext(LocalContext.current.applicationContext)
}

fun Koin.androidContext(androidContext: Context) {
    if (androidContext is Application) {
        loadModules(listOf(module {
            single { androidContext } bind Context::class
        }))
    } else {
        loadModules(listOf(module {
            single { androidContext }
        }))
    }
}
