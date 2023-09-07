package org.koin.mp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.KoinExperimentalAPI

@KoinExperimentalAPI
actual object KoinPlatformCoroutinesTools {
    actual fun defaultCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
