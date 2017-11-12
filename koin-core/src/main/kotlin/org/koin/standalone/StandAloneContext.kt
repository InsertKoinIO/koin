package org.koin.standalone

import org.koin.Koin
import org.koin.KoinContext
import org.koin.dsl.module.Module

/**
 * Koin agnostic context support
 * @author - Arnaud GIULIANI
 */
object StandAloneContext {
    /**
     * Koin Context
     */
    lateinit var koinContext: StandAloneKoinContext
}

/**
 * Stand alone Koin context
 */
interface StandAloneKoinContext

///**
// * Dry Run starter
// * Test if each bean definition can be created/injected
// */
//fun dryRun(list: List<Module>) {
//    startContext(list)
//    (StandAloneContext.koinContext as KoinContext).dryRun()
//}