package org.koin.test.core

import org.koin.core.Koin
import org.koin.core.KoinContext
import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceRegistry
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.core.path.PathRegistry
import org.koin.core.property.PropertyRegistry
import org.koin.core.scope.ScopeRegistry
import org.koin.dsl.module.Module
import org.koin.log.Logger
import org.koin.standalone.StandAloneContext
import org.koin.test.core.instance.SandboxInstanceFactory
import org.koin.test.ext.koin.dryRun

/**
 * Check all definition's dependencies
 */
fun StandAloneContext.checkModules(list: List<Module>, logger: Logger) {
    Koin.logger = logger //PrintLogger(showDebug = true)
    Koin.logger.info("[Sandbox]")
    koinContext =
            KoinContext(
                InstanceRegistry(
                    BeanRegistry(),
                    SandboxInstanceFactory(),
                    PathRegistry()
                ), ScopeRegistry(), PropertyRegistry()
            )

    // Build list
    Koin(koinContext as KoinContext).build(list)

    // Run checks
    (koinContext as KoinContext).dryRun(emptyParameterDefinition())
}