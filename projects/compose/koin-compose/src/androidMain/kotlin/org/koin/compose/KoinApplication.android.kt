package org.koin.compose

import androidx.compose.runtime.Composable
import org.koin.core.KoinApplication
import org.koin.dsl.KoinConfiguration
import androidx.compose.ui.platform.LocalContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.dsl.koinConfiguration
import org.koin.dsl.includes

@Composable
internal actual fun composeConfiguration(loggerLevel : Level,config : KoinApplication.() -> Unit) : KoinConfiguration {
    val current = LocalContext.current
    return koinConfiguration {
        androidContext(current)
        androidLogger(loggerLevel)
        includes(config)
    }
}