package org.koin.compose

import androidx.compose.runtime.Composable
import org.koin.core.KoinApplication
import org.koin.dsl.KoinConfiguration
import org.koin.core.logger.Level
import org.koin.dsl.koinConfiguration
import org.koin.dsl.includes

@Composable
internal actual fun composeConfiguration(loggerLevel : Level,config : KoinApplication.() -> Unit) : KoinConfiguration {
    return koinConfiguration {
        printLogger(loggerLevel)
        includes(config)
    }
}