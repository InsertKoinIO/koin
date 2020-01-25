package org.koin.core.logger

/**
 * Logger that print on system.out
 * @author - Arnaud GIULIANI
 */
expect class PrintLogger(level: Level = Level.INFO) : Logger