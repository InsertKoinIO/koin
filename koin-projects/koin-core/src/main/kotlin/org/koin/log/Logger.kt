package org.koin.log

/**
 * A simple Logger
 * @author - Arnaud GIULIANI
 * @author - Laurent BARESSE
 */
interface Logger {
    /**
     * Normal log
     */
    fun log(msg : String)

    /**
     * Debug log
     */
    fun debug(msg : String)

    /**
     * Error log
     */
    fun err(msg : String)
}