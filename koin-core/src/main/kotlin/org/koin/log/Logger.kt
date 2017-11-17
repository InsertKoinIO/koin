package org.koin.log

/**
 * A simple Logger
 */
interface Logger {
    /**
     * Normal log
     */
    fun log(msg : String)

    /**
     * Error log
     */
    fun err(msg : String)
}