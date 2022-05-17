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
import org.koin.core.definition._createDefinition
import org.koin.core.definition.indexKey
import org.koin.core.instance.InstanceContext
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ScopedInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.module.overrideError
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import org.koin.mp.KoinPlatformTools.safeHashMap
import kotlin.reflect.KClass

@OptIn(KoinInternalApi::class)
class InstanceRegistry(val _koin: Koin) {

    private val _instances = safeHashMap<IndexKey, InstanceFactory<*>>()
    val instances: Map<IndexKey, InstanceFactory<*>>
        get() = _instances

    private val eagerInstances = hashSetOf<SingleInstanceFactory<*>>()

    internal fun loadModules(modules: Set<Module>, allowOverride: Boolean) {
        modules.forEach { module ->
            loadModule(module, allowOverride)
            eagerInstances.addAll(module.eagerInstances)
        }
    }

    internal fun createAllEagerInstances() {
        createEagerInstances(eagerInstances)
        eagerInstances.clear()
    }

    private fun loadModule(module: Module, allowOverride: Boolean) {
        module.mappings.forEach { (mapping, factory) ->
            saveMapping(allowOverride, mapping, factory)
        }
    }

    @KoinInternalApi
    fun saveMapping(
        allowOverride: Boolean,
        mapping: IndexKey,
        factory: InstanceFactory<*>,
        logWarning: Boolean = true
    ) {
        if (_instances.containsKey(mapping)) {
            if (!allowOverride) {
                overrideError(factory, mapping)
            } else {
                if (logWarning) _koin.logger.info("Override Mapping '$mapping' with ${factory.beanDefinition}")
            }
        }
        if (_koin.logger.isAt(Level.DEBUG) && logWarning) {
            _koin.logger.debug("add mapping '$mapping' for ${factory.beanDefinition}")
        }
        _instances[mapping] = factory
    }

    private fun createEagerInstances(eagerInstances: HashSet<SingleInstanceFactory<*>>) {
        if (eagerInstances.isNotEmpty()) {
            if (_koin.logger.isAt(Level.DEBUG)) {
                _koin.logger.debug("Creating eager instances ...")
            }
            val defaultContext = InstanceContext(_koin, _koin.scopeRegistry.rootScope)
            eagerInstances.forEach { factory ->
                factory.get(defaultContext)
            }
        }
    }

    internal fun resolveDefinition(
        clazz: KClass<*>,
        qualifier: Qualifier?,
        scopeQualifier: Qualifier
    ): InstanceFactory<*>? {
        val indexKey = indexKey(clazz, qualifier, scopeQualifier)
        return _instances[indexKey]
    }

    internal fun <T> resolveInstance(
        qualifier: Qualifier?,
        clazz: KClass<*>,
        scopeQualifier: Qualifier,
        instanceContext: InstanceContext
    ): T? {
        return resolveDefinition(clazz, qualifier, scopeQualifier)?.get(instanceContext) as? T
    }

    @PublishedApi
    internal inline fun <reified T> declareScopedInstance(
        instance: T,
        qualifier: Qualifier? = null,
        secondaryTypes: List<KClass<*>> = emptyList(),
        allowOverride: Boolean = true,
        scopeQualifier: Qualifier,
        scopeID: ScopeID
    ) {
        val def = _createDefinition(Kind.Scoped, qualifier, { instance }, secondaryTypes, scopeQualifier)
        val indexKey = indexKey(def.primaryType, def.qualifier, def.scopeQualifier)
        val existingFactory = instances[indexKey] as? ScopedInstanceFactory
        if (existingFactory != null){
            existingFactory.refreshInstance(scopeID,instance as Any)
        } else {
            val factory = ScopedInstanceFactory(def)
            saveMapping(allowOverride, indexKey, factory)
            def.secondaryTypes.forEach { clazz ->
                val index = indexKey(clazz, def.qualifier, def.scopeQualifier)
                saveMapping(allowOverride, index, factory)
            }
        }

    }

    @PublishedApi
    internal inline fun <reified T> declareRootInstance(
        instance: T,
        qualifier: Qualifier? = null,
        secondaryTypes: List<KClass<*>> = emptyList(),
        allowOverride: Boolean = true
    ) {
        val rootQualifier = _koin.scopeRegistry.rootScope.scopeQualifier
        val def = _createDefinition(Kind.Scoped, qualifier, { instance }, secondaryTypes, rootQualifier)
        val factory = SingleInstanceFactory(def)
        val indexKey = indexKey(def.primaryType, def.qualifier, def.scopeQualifier)
        saveMapping(allowOverride, indexKey, factory)
        def.secondaryTypes.forEach { clazz ->
            val index = indexKey(clazz, def.qualifier, def.scopeQualifier)
            saveMapping(allowOverride, index, factory)
        }
    }


    internal fun dropScopeInstances(scope: Scope) {
        _instances.values.filterIsInstance<ScopedInstanceFactory<*>>().forEach { factory -> factory.drop(scope) }
    }

    internal fun close() {
        _instances.forEach { (key, factory) ->
            factory.dropAll()
        }
        _instances.clear()
    }

    internal fun <T> getAll(clazz: KClass<*>, instanceContext: InstanceContext): List<T> {
        return _instances.values
            .filter { factory ->
                factory.beanDefinition.scopeQualifier == instanceContext.scope.scopeQualifier
            }
            .filter { factory ->
                factory.beanDefinition.primaryType == clazz || factory.beanDefinition.secondaryTypes.contains(
                    clazz
                )
            }
            .distinct()
            .map { it.get(instanceContext) as T }
    }

    internal fun unloadModules(modules: Set<Module>) {
        modules.forEach { unloadModule(it) }
    }

    private fun unloadModule(module: Module) {
        module.mappings.keys.forEach { mapping ->
            if (_instances.containsKey(mapping)) {
                _instances[mapping]?.dropAll()
                _instances.remove(mapping)
            }
        }
    }

    fun size(): Int {
        return _instances.size
    }
}