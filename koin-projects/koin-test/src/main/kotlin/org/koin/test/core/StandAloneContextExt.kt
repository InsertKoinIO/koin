package org.koin.test.core

import org.koin.core.InstanceManager
import org.koin.core.Koin
import org.koin.core.KoinContext
import org.koin.core.bean.BeanRegistry
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.core.path.PathRegistry
import org.koin.core.property.PropertyRegistry
import org.koin.dsl.module.Module
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext
import org.koin.test.core.instance.SandboxInstanceFactory
import org.koin.test.ext.koin.dryRun

/**
 * Check all definition's dependencies
 */
fun StandAloneContext.check(list: List<Module>) {
    Koin.logger = PrintLogger(showDebug = true)
    Koin.logger.info("[Sandbox]")
    koinContext =
            KoinContext(InstanceManager(BeanRegistry(), SandboxInstanceFactory(), PathRegistry()), PropertyRegistry())

    // Build list
    Koin(koinContext as KoinContext).build(list)

    // Run checks
    (koinContext as KoinContext).dryRun(emptyParameterDefinition())
}