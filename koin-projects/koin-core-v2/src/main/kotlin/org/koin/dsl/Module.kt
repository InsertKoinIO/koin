package org.koin.dsl

import org.koin.core.Module

fun module(moduleDeclaration: Module.() -> Unit): Module {
    return Module().apply(moduleDeclaration)
}