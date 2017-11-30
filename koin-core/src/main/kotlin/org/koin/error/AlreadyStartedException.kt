package org.koin.error

/**
 * Koin has already been started
 */
class AlreadyStartedException(msg: String = "Koin has already been started!") : Exception(msg)