package org.koin.mp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual object KoinPlatformCoroutinesTools {
    actual fun defaultCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
