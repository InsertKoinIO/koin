package org.koin.test

import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.test.ext.koin.check
import org.koin.test.ext.koin.dryRun

/**
 * Koin Test Component
 */
interface KoinTest : KoinComponent

/**
 * Check all definition's dependencies
 */
fun KoinTest.check() {
    (StandAloneContext.koinContext as KoinContext).check()
}

/**
 * Dry Run - Try to create each definition
 */
fun KoinTest.dryRun(defaultParameters: ParameterDefinition = emptyParameterDefinition()) {
    (StandAloneContext.koinContext as KoinContext).dryRun(defaultParameters)
}