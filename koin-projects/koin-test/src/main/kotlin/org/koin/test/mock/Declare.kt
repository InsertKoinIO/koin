package org.koin.test.mock

import org.koin.core.context.GlobalContext
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module
import org.koin.test.KoinTest

/**
 * Declare component on the fly
 * @param moduleDeclaration lambda
 */
fun KoinTest.declare(moduleDeclaration: ModuleDeclaration) {
    val module = module(override = true, moduleDeclaration = moduleDeclaration)
    GlobalContext.get().modules(module)
}