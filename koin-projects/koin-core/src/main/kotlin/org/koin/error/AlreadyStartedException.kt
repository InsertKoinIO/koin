package org.koin.error

/**
 * Koin has already been started
 *
 * @author - Arnaud GIULIANI
 * @author - Laurent BARESSE
 */
class AlreadyStartedException(msg: String = "Koin has already been started!") : Exception(msg)