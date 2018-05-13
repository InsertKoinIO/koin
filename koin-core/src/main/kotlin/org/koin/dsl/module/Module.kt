package org.koin.dsl.module

import org.koin.core.KoinContext
import org.koin.core.scope.Scope
import org.koin.dsl.context.ModuleDefinition
import org.koin.standalone.StandAloneContext


/**
 * Create ModuleDefinition
 */
@Deprecated(
    "use module function instead", ReplaceWith(
        "module { ModuleDefinition(Scope.ROOT, StandAloneContext.koinContext as KoinContext).apply(init) }",
        "org.koin.dsl.context.ModuleDefinition",
        "org.koin.core.scope.Scope",
        "org.koin.standalone.StandAloneContext",
        "org.koin.core.KoinContext"
    )
)
fun applicationContext(init: ModuleDefinition.() -> Unit): Module =
    { ModuleDefinition(Scope.ROOT, StandAloneContext.koinContext as KoinContext).apply(init) }

fun module(init: ModuleDefinition.() -> Unit): Module =
    { ModuleDefinition(Scope.ROOT, StandAloneContext.koinContext as KoinContext).apply(init) }

/**
 * Module - function that gives a module
 */
typealias Module = () -> ModuleDefinition