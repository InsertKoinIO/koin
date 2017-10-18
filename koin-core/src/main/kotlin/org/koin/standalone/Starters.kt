package org.koin.standalone

import org.koin.Koin
import org.koin.dsl.module.Module

/**
 * Koin Context builder
 */
fun startContext(list: List<Module>) {
    StandAloneRegistry.koinContext = Koin().build(list)
}

/**
 * Koin Context builder
 */
fun startContext(vararg list: Module) {
    StandAloneRegistry.koinContext = Koin().build(*list)
}