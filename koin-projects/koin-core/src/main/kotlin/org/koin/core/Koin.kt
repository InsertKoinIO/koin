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

import org.koin.core.instance.ModuleCallBack
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.core.scope.ScopeCallback
import org.koin.core.time.measureDuration
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.module.Module
import org.koin.dsl.path.Path
import org.koin.log.EmptyLogger
import org.koin.log.Logger
import java.util.*


/**
 * Koin Context Builder API
 *
 * @author - Arnaud GIULIANI
 * @author - Laurent BARESSE
 */
class Koin private constructor(val koinContext: KoinContext) {

    val propertyResolver = koinContext.propertyResolver
    val beanRegistry = koinContext.instanceRegistry.beanRegistry
    val pathRegistry = koinContext.instanceRegistry.pathRegistry
    val instanceFactory = koinContext.instanceRegistry.instanceFactory

    /**
     * Load Koin properties - whether Koin is already started or not
     * Will look at koin.properties file
     *
     * @param useEnvironmentProperties - environment properties
     * @param useKoinPropertiesFile - koin.properties file
     * @param extraProperties - additional properties
     */
    fun loadProperties(koinProps: KoinProperties): Koin = synchronized(this) {
        if (koinProps.useKoinPropertiesFile) {
            Koin.logger.info("[properties] load koin.properties")
            bindKoinProperties()
        }

        if (koinProps.extraProperties.isNotEmpty()) {
            Koin.logger.info("[properties] load extras properties : ${koinProps.extraProperties.size}")
            loadExtraProperties(koinProps.extraProperties)
        }

        if (koinProps.useEnvironmentProperties) {
            Koin.logger.info("[properties] load environment properties")
            bindEnvironmentProperties()
        }
        return this
    }


    /**
     * load extra properties
     */
    fun loadExtraProperties(props: Map<String, Any>): Koin {
        if (props.isNotEmpty()) {
            propertyResolver.addAll(props)
        }
        return this
    }


    private fun bindKoinProperties(koinFile: String = "/koin.properties"): Koin {
        val content = Koin::class.java.getResource(koinFile)?.readText()
        content?.let {
            val koinProperties = Properties()
            koinProperties.load(content.byteInputStream())
            val nb = propertyResolver.import(koinProperties)
            logger.info("[properties] loaded $nb properties from '$koinFile' file")
        }
        return this
    }


    private fun bindEnvironmentProperties(): Koin {
        val n1 = propertyResolver.import(System.getProperties())
        val n2 = propertyResolver.import(System.getenv().toProperties())
        logger.info("[properties] loaded $n1 properties from properties")
        logger.info("[properties] loaded $n2 properties from env properties")
        return this
    }

    /**
     * load given list of module instances into current StandAlone koin context
     */
    fun loadModules(modules: Collection<Module>): Koin {
        val duration = measureDuration {
            modules.forEach { module ->
                registerDefinitions(module(koinContext))
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
        val modulePath: Path =
            pathRegistry.makePath(moduleDefinition.path, parentModuleDefinition?.path)
        val consolidatedPath =
            if (path != Path.root()) modulePath.copy(parent = path) else modulePath

        pathRegistry.savePath(consolidatedPath)

        // Add definitions & propagate eager/override
        moduleDefinition.definitions.forEach { definition ->
            val eager =
                if (moduleDefinition.createOnStart) moduleDefinition.createOnStart else definition.isEager
            val override =
                if (moduleDefinition.override) moduleDefinition.override else definition.allowOverride
            val name = if (definition.name.isEmpty()) {
                val pathString =
                    if (consolidatedPath == Path.Companion.root()) "" else "$consolidatedPath."
                "$pathString${definition.primaryTypeName}"
            } else definition.name
            val def = definition.copy(
                name = name,
                isEager = eager,
                allowOverride = override,
                path = consolidatedPath
            )
            instanceFactory.delete(def)
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

    /**
     * Create instances for definitions tagged as `eager`
     *
     * @param defaultParameters - default injection parameters
     */
    fun createEagerInstances(defaultParameters: ParameterDefinition = emptyParameterDefinition()) {
        koinContext.instanceRegistry.createEagerInstances(defaultParameters)
    }

    /**
     * Register ScopeCallback
     */
    fun registerScopeCallback(callback: ScopeCallback) {
        koinContext.scopeRegistry.register(callback)
    }

    /**
     * Register ModuleCallBack
     */
    fun registerModuleCallBack(callback: ModuleCallBack) {
        koinContext.instanceRegistry.instanceFactory.register(callback)
    }

    /**
     * Close Koin instance
     */
    fun close() {
        koinContext.close()
    }

    companion object {
        /**
         * Koin Logger
         */
        var logger: Logger = EmptyLogger()

        /**
         * Create Koin instance
         */
        fun create(koinContext: KoinContext = KoinContext.create()) = Koin(koinContext)
    }
}