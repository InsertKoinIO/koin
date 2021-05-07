/*
 * Copyright 2017-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.test.check

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.instance.InstanceContext
import org.koin.core.logger.Level
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication
import org.koin.mp.KoinPlatformTools

/**
 * Check all definition's dependencies - start all modules and check if definitions can run
 */
fun KoinApplication.checkModules() = koin.checkModules()

/**
 *
 */
fun checkModules(level: Level = Level.INFO, appDeclaration: KoinAppDeclaration) {
    koinApplication(appDeclaration)
        .logger(KoinPlatformTools.defaultLogger(level))
        .checkModules()
}

/**
 * Check all definition's dependencies - start all modules and check if definitions can run
 */
@OptIn(KoinInternalApi::class)
fun Koin.checkModules() {
    logger.info("[Check] checking current modules ...")
    instanceRegistry.instances.forEach { (mapping, factory) ->
        logger.info("[Check] $mapping ...")
        val def = factory.beanDefinition
        val scope = if (def.scopeQualifier != scopeRegistry.rootScope.scopeQualifier) {
            getOrCreateScope(def.scopeQualifier.value, def.scopeQualifier)
        } else scopeRegistry.rootScope
        val ctx = InstanceContext(this, scope)
        factory.get(ctx)
    }
    logger.info("[Check] modules checked")
    close()
}
