package org.koin.standalone

/**
 * releaseContext any context
 */
fun releaseContext(name: String = "") = StandAloneRegistry.koinContext.release(name)