package org.koin.test.components

import org.koin.standalone.StandAloneContext

/**
 * Get Koin in test components
 */
fun KoinTest.getKoin() = StandAloneContext.koinContext

/**
 * inject any component in KoinTest
 */
inline fun <reified R> KoinTest.inject(name: String = "") = lazy { getKoin().get<R>(name) }

/**
 * inject any property in KoinTest
 */
inline fun <reified R> KoinTest.property(name: String = "") = lazy { getKoin().getProperty<R>(name) }
