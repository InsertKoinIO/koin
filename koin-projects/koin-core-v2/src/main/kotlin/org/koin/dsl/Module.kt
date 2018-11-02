package org.koin.dsl

import org.koin.core.module.Module

fun module(moduleDeclaration: Module.() -> Unit): Module {
    return Module().apply(moduleDeclaration)
}