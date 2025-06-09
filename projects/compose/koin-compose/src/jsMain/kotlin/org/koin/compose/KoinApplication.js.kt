package org.koin.compose

import androidx.compose.runtime.Composable
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.core.logger.Level
import org.koin.dsl.koinConfiguration
import org.koin.dsl.includes
import org.koin.mp.KoinPlatform

@Composable
@KoinExperimentalAPI
internal actual fun composeMultiplatformConfiguration(loggerLevel : Level, config : KoinConfiguration) : KoinConfiguration {
    return koinConfiguration {
        printLogger(loggerLevel)
        includes(config)
    }
}

@Composable
internal actual fun retrieveDefaultInstance() : Koin {
    return KoinPlatform.getKoin()
}