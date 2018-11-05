package org.koin.dsl

import org.koin.core.module.Module

typealias ModuleDeclaration = Module.() -> Unit

fun module(isCreatedAtStart: Boolean = false, override: Boolean = false, moduleDeclaration: ModuleDeclaration): Module {
    return Module(isCreatedAtStart, override).apply(moduleDeclaration)
}