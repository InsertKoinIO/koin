package org.koin.dsl

import org.koin.core.module.Module

typealias ModuleDeclaration = Module.() -> Unit

fun module(createdAtStart: Boolean = false, override: Boolean = false, moduleDeclaration: ModuleDeclaration): Module {
    return Module(createdAtStart, override).apply(moduleDeclaration)
}