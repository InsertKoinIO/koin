package org.koin.test

import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext

/**
 * Koin Test Component
 */
interface KoinTest : KoinComponent

/**
 * Make a Dry Run - Test if each beanDefinition is injectable
 */
fun KoinTest.dryRun(defaultParameters: ParameterDefinition = emptyParameterDefinition()) {
    (StandAloneContext.koinContext as KoinContext).dryRun(defaultParameters)
}

