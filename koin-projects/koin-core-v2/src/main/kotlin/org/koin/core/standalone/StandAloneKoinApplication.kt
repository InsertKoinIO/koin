package org.koin.core.standalone

import org.koin.core.KoinApplication

object StandAloneKoinApplication {
    internal var app: KoinApplication? = null

    fun get(): KoinApplication = app ?: error("KoinApplication has not been initialized")
}