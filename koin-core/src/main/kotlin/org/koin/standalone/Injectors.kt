package org.koin.standalone

/**
 * Get Koin context everywhere
 */
fun <T> T.getKoin() = StandAloneRegistry.koinContext

/**
 * inject any component
 */
inline fun <T, reified R> T.inject(name: String = "") = lazy { getKoin().get<R>(name) }

/**
 * inject any property
 */
inline fun <T, reified R> T.property(name: String = "") = lazy { getKoin().getProperty<R>(name) }
