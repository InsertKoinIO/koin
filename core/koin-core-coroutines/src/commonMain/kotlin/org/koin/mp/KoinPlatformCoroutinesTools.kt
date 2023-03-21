package org.koin.mp

import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.KoinExperimentalAPI

@KoinExperimentalAPI
expect object KoinPlatformCoroutinesTools {
    fun defaultCoroutineDispatcher() : CoroutineDispatcher
}