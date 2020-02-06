package org.koin.core.context

import org.koin.core.Koin
import org.koin.core.KoinApplication

object KoinContextHandler {

    private var _context: KoinContext? = null

    private fun getContext(): KoinContext {
        return _context ?: error("No Koin Context configured. Please use startKoin or koinApplication DSL. ")
    }

    /**
     * StandAlone Koin App instance
     */
    fun get(): Koin = getContext().get()

    /**
     * StandAlone Koin App instance
     */
    fun getOrNull(): Koin? = _context?.getOrNull()


    fun register(koinContext: KoinContext) = synchronized(this){
        if (_context != null){
            error("A KoinContext is already started")
        }
        _context = koinContext
    }

    /**
     * Start a Koin Application as StandAlone
     */
    fun start(koinApplication: KoinApplication) {
        getContext().setup(koinApplication)
    }

    /**
     * Stop current StandAlone Koin application
     */
    fun stop() {
        _context?.stop()
        _context = null
    }

}