package org.koin.dsl.module

import org.koin.KoinContext
import org.koin.core.scope.Scope
import org.koin.dsl.context.Context
import org.koin.standalone.StandAloneContext


/**
 * Create Context
 */
fun applicationContext(init: Context.() -> Unit) = kotlin.lazy { Context(Scope.ROOT, StandAloneContext.koinContext as KoinContext).apply(init) }

/**
 * Module - lazy evaluated Context
 */
typealias Module = Lazy<Context>
