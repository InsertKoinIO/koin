package org.koin.test.components

import org.koin.standalone.StandAloneContext

/**
 * Get Koin context everywhere
 */
fun TestComponent.getKoin() = StandAloneContext.koinContext

/**
 * inject any component
 */
inline fun <reified R> TestComponent.inject(name: String = "") = lazy { getKoin().get<R>(name) }

/**
 * inject any property
 */
inline fun <reified R> TestComponent.property(name: String = "") = lazy { getKoin().getProperty<R>(name) }
