/*
 * Copyright 2017-2018 the original author or authors.
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
package org.koin.core

import org.koin.core.time.measureDuration
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.module.Module
import org.koin.dsl.path.Path
import org.koin.log.EmptyLogger
import org.koin.log.Logger
import java.util.*


/**
 * Koin Context & Module Builder
 *
 * @author - Arnaud GIULIANI
 * @author - Laurent BARESSE
 */
class Koin(val koinContext: KoinContext) {

    val propertyResolver = koinContext.propertyResolver
    val beanRegistry = koinContext.instanceManager.beanRegistry
    val pathRegistry = koinContext.instanceManager.pathRegistry

    /**
     * Inject properties to context
     */
    fun bindAdditionalProperties(props: Map<String, Any>): Koin {
        if (props.isNotEmpty()) {
            propertyResolver.addAll(props)
        }
        return this
    }

    /**
     * Inject all properties from koin properties file to context
     */
    fun bindKoinProperties(koinFile: String = "/koin.properties"): Koin {
        val content = Koin::class.java.getResource(koinFile)?.readText()
        content?.let {
            val koinProperties = Properties()
            koinProperties.load(content.byteInputStream())
            val nb = propertyResolver.import(koinProperties)
            logger.info("[properties] loaded $nb properties from '$koinFile' file")
        }
        return this
    }

    /**
     * Inject all system properties to context
     */
    fun bindEnvironmentProperties(): Koin {
        val n1 = propertyResolver.import(System.getProperties())
        logger.info("[properties] loaded $n1 properties from properties")
        val n2 = propertyResolver.import(System.getenv().toProperties())
        logger.info("[properties] loaded $n2 properties from env properties")
        return this
    }

    /**
     * load given list of module instances into current StandAlone koin context
     */
    fun build(modules: Collection<Module>): Koin {
        val duration = measureDuration {
            modules.forEach { module ->
                registerDefinitions(module())
            }

            logger.info("[modules] loaded ${beanRegistry.definitions.size} definitions")
        }
        logger.debug("[modules] loaded in $duration ms")
        return this
    }

    /**
     * Register moduleDefinition definitions & subModules
     */
    private fun registerDefinitions(
        moduleDefinition: ModuleDefinition,
        parentModuleDefinition: ModuleDefinition? = null,
        path: Path = Path.root()
    ) {
        val modulePath: Path = pathRegistry.makePath(moduleDefinition.path, parentModuleDefinition?.path)
        val consolidatedPath = if (path != Path.root()) modulePath.copy(parent = path) else modulePath

        pathRegistry.savePath(consolidatedPath)

        // Add definitions & propagate eager/override
        moduleDefinition.definitions.forEach { definition ->
            val eager = if (moduleDefinition.createOnStart) moduleDefinition.createOnStart else definition.isEager
            val override = if (moduleDefinition.override) moduleDefinition.override else definition.allowOverride
            val def = definition.copy(isEager = eager, allowOverride = override, path = consolidatedPath)
            beanRegistry.declare(def)
        }

        // Check sub contexts
        moduleDefinition.subModules.forEach { subModule ->
            registerDefinitions(
                subModule,
                moduleDefinition,
                consolidatedPath
            )
        }
    }

    companion object {
        /**
         * Koin Logger
         */
        var logger: Logger = EmptyLogger()
    }
}