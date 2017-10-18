package org.koin.standalone

/**
 * Get Koin context everywhere
 */
fun <T> T.getKoin() = StandAloneRegistry.koinContext

/**
 * releaseContext any context
 */
fun releaseContext(name: String = "") = StandAloneRegistry.koinContext.release(name)

/**
 * inject any component
 */
inline fun <T, reified R> T.inject(name: String = "") = lazy { getKoin().get<R>(name) }

/**
 * inject any property
 */
inline fun <T, reified R> T.property(name: String = "") = lazy { getKoin().getProperty<R>(name) }
