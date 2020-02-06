package org.koin.core.context

import org.koin.core.Koin
import org.koin.core.KoinApplication

interface KoinContext {
    /**
     * StandAlone Koin App instance
     */
    fun get(): Koin

    /**
     * StandAlone Koin App instance
     */
    fun getOrNull(): Koin?

    /**
     * Start a Koin Application as StandAlone
     */
    fun setup(koinApplication: KoinApplication)

    /**
     * Stop current StandAlone Koin application
     */
    fun stop()
}