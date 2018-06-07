package org.koin.dsl.module

import org.koin.KoinContext
import org.koin.core.scope.Scope
import org.koin.dsl.context.Context
import org.koin.standalone.StandAloneContext


/**
 * Create Context
 */
fun applicationContext(override: Boolean = false,
                       init: Context.() -> Unit): Module = { Context(Scope.ROOT, StandAloneContext.koinContext as KoinContext, override).apply(init) }

/**
 * Module - function that gives a module
 */
typealias Module = () -> Context
