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
package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.IndexKey
import org.koin.core.definition.Kind
import org.koin.core.definition.createDefinition
import org.koin.core.definition.indexKey
import org.koin.core.instance.InstanceContext
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ScopedInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.module.Module
import org.koin.core.module.overrideError
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatformTools.safeHashMap
import kotlin.reflect.KClass

@OptIn(KoinInternalApi::class)
class InstanceRegistry(val _koin: Koin) {

    private val _instances = safeHashMap<IndexKey, InstanceFactory<*>>()
    val instances: Map<IndexKey, InstanceFactory<*>>
        get() = _instances

    internal fun loadModules(modules: List<Module>, allowOverride: Boolean) {
        modules.forEach { module ->
            loadModule(module, allowOverride)
            createEagerInstances(module.eagerInstances)
        }
    }

    private fun loadModule(module: Module, allowOverride: Boolean) {
        if (!module.isLoaded) {
            module.mappings.forEach { (mapping, factory) ->
                saveMapping(allowOverride, mapping, factory)
            }
            module.isLoaded = true
        }
    }

    @PublishedApi
    internal fun saveMapping(
        allowOverride: Boolean,
        mapping: IndexKey,
        factory: InstanceFactory<*>
    ) {
        if (!allowOverride && _instances.containsKey(mapping)) {
            overrideError(factory, mapping)
        } else {
            _koin.logger.info("")
        }
        _instances[mapping] = factory
    }

    private fun createEagerInstances(eagerInstances: HashSet<SingleInstanceFactory<*>>) {
        val defaultContext = InstanceContext(_koin, _koin.scopeRegistry.rootScope)
        eagerInstances.forEach { factory ->
            factory.create(defaultContext)
        }
    }


    internal fun <T> resolveInstance(
        qualifier: Qualifier?,
        clazz: KClass<*>,
        scopeQualifier: Qualifier,
        instanceContext: InstanceContext
    ): T? {
        val indexKey = indexKey(clazz, qualifier, scopeQualifier)
        return _instances[indexKey]?.get(instanceContext) as? T
    }

    @PublishedApi
    internal inline fun <reified T> declareInstance(
        instance: T,
        qualifier: Qualifier? = null,
        secondaryTypes: List<KClass<*>> = emptyList(),
        override: Boolean = true,
        scopeQualifier: Qualifier
    ) {
        val def = createDefinition(Kind.Scoped, qualifier, { instance }, secondaryTypes, scopeQualifier)
        val factory = ScopedInstanceFactory(def)
        val indexKey = indexKey(def.primaryType, def.qualifier, def.scopeQualifier)
        saveMapping(override, indexKey, factory)
        def.secondaryTypes.forEach { clazz ->
            val index = indexKey(clazz, def.qualifier, def.scopeQualifier)
            saveMapping(override, index, factory)
        }
    }


    internal fun dropScopeInstances(scope: Scope) {
        _instances.values.filterIsInstance<ScopedInstanceFactory<*>>().forEach { factory -> factory.drop(scope) }
    }

    internal fun close() {
        _instances.clear()
    }

    fun <T> getAll(clazz: KClass<*>): List<T> {
        TODO("Not yet implemented")
    }

    fun unloadModules(modules: List<Module>) {
        TODO("Not yet implemented")
    }

    fun size(): Int {
        return _instances.size
    }

}