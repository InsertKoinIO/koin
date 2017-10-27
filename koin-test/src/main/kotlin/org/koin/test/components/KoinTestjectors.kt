package org.koin.test.components

import org.koin.Koin
import org.koin.dsl.module.Module
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

/**
 * KoinTest Context builder
 */
fun KoinTest.startContext(list: List<Module>) {
    StandAloneContext.koinContext = Koin().build(list)
}

/**
 * KoinTest Context builder
 */
fun KoinTest.startContext(vararg list: Module) {
    StandAloneContext.koinContext = Koin().build(*list)
}