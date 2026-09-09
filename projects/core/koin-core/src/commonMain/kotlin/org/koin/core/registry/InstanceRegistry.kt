/*
 * Copyright 2017-Present the original author or authors.
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
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.IndexKey
import org.koin.core.definition.Kind
import org.koin.core.definition._createDefinition
import org.koin.core.definition.indexKey
import org.koin.core.instance.ResolutionContext
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.NoClass
import org.koin.core.instance.ScopedInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.module.throwOverrideError
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import org.koin.ext.getFullName
import org.koin.mp.KoinPlatformTools
import org.koin.mp.KoinPlatformTools.safeHashMap
import kotlin.collections.set
import kotlin.collections.toTypedArray
import kotlin.reflect.KClass
import org.koin.mp.Lockable

// A class with more (scope, qualifier) variants than this stays on the String map (KTZ-4829).
private const val MAX_TYPED_CHAIN = 8

@Suppress("UNCHECKED_CAST")
@OptIn(KoinInternalApi::class)
class InstanceRegistry(val _koin: Koin) {

    private val _instances = safeHashMap<IndexKey, InstanceFactory<*>>()
    val instances: Map<IndexKey, InstanceFactory<*>>
        get() = _instances

    // Lazily populated, allocation-free lookup cache in front of _instances (KTZ-4829).
    //
    // The String IndexKey is "<class full name>:<type qualifier value>:<scope qualifier>", and
    // building it was ~160 of the ~200 bytes a warm get() still allocated. Entries here hold the
    // same three components already split, so a repeat lookup is one map get plus a short chain
    // walk with no allocation. Populated only from a String-path hit, never at registration:
    // in a startup run every definition is resolved once, so an eagerly maintained mirror
    // measured as pure cost there (+74% on registration).
    //
    // _instances stays the single source of truth. Every write to it drops the chains for the
    // factory's class names (over-invalidation is safe, it only costs a re-populate), a populate
    // re-checks the String map under the same lock, and a miss always falls through to the
    // String map — so a caller hand-building keys keeps today's behavior to the byte.
    private val typedIndex = safeHashMap<String, TypedEntry>()
    private val typedIndexLock = Lockable()

    private class TypedEntry(
        val scopeKey: String,
        val qualifierKey: String,
        val factory: InstanceFactory<*>,
        val next: TypedEntry?,
    )

    private val eagerInstances = safeHashMap<Int, SingleInstanceFactory<*>>()

    internal fun loadModules(modules: Set<Module>, allowOverride: Boolean) {
        modules.forEach { module ->
            loadModule(module, allowOverride)
            addAllEagerInstances(module)
        }
    }

    private fun addAllEagerInstances(module: Module) {
        module.eagerInstances.forEach { factory ->
            eagerInstances[factory.beanDefinition.hashCode()] = factory
        }
    }

    internal fun createAllEagerInstances() {
        val instances = arrayListOf(*eagerInstances.values.toTypedArray())
        eagerInstances.clear()
        createEagerInstances(instances)
    }

    private fun loadModule(module: Module, allowOverride: Boolean) {
        module.mappings.forEach { (mapping, factory) ->
            val hasFactoryAllowOverride = factory.beanDefinition.allowOverride == true
            val override = allowOverride || hasFactoryAllowOverride
            saveMapping(override, mapping, factory)

        }
    }

    @KoinInternalApi
    fun saveMapping(
        allowOverride: Boolean,
        mapping: IndexKey,
        factory: InstanceFactory<*>,
        logWarning: Boolean = true,
    ) {
        _instances[mapping]?.let {
            if (!allowOverride) {
                throwOverrideError(factory, mapping)
            } else if (logWarning) {
                _koin.logger.log(Level.WARNING) { "(+) override index '$mapping' -> '${factory.beanDefinition}'" }
                // remove previous eager isntance too
                val existingFactory = eagerInstances.values.firstOrNull { it.beanDefinition == factory.beanDefinition }
                if (existingFactory != null) {
                    eagerInstances.remove(factory.beanDefinition.hashCode())
                }
            }
        }
        _koin.logger.log(Level.DEBUG) { "(+) index '$mapping' -> '${factory.beanDefinition}'" }
        val previous = _instances.put(mapping, factory)
        forget(factory)
        if (previous != null && previous !== factory) forget(previous)
    }

    private fun remember(className: String, scopeKey: String, qualifierKey: String, key: IndexKey, factory: InstanceFactory<*>) {
        KoinPlatformTools.synchronized(typedIndexLock) {
            // A writer may have replaced the key between our String-path read and this lock.
            if (_instances[key] !== factory) return@synchronized
            val head = typedIndex[className]
            var length = 0
            var entry = head
            while (entry != null) {
                if (entry.scopeKey == scopeKey && entry.qualifierKey == qualifierKey) return@synchronized
                length++
                entry = entry.next
            }
            // A class with many (scope, qualifier) variants stays on the String map rather than
            // turning every lookup into a long chain walk.
            if (length >= MAX_TYPED_CHAIN) return@synchronized
            typedIndex[className] = TypedEntry(scopeKey, qualifierKey, factory, head)
        }
    }

    // Called after every write to _instances involving this factory.
    private fun forget(factory: InstanceFactory<*>) {
        if (typedIndex.isEmpty()) return
        KoinPlatformTools.synchronized(typedIndexLock) {
            val definition = factory.beanDefinition
            typedIndex.remove(definition.primaryType.getFullName())
            definition.secondaryTypes.forEach { typedIndex.remove(it.getFullName()) }
        }
    }

    private fun createEagerInstances(instances: Collection<SingleInstanceFactory<*>>) {
        val defaultContext = ResolutionContext(_koin.logger, _koin.scopeRegistry.rootScope, clazz = NoClass::class)
        instances.forEach { factory -> factory.get(defaultContext) }
    }

    internal fun resolveDefinition(
        clazz: KClass<*>,
        qualifier: Qualifier?,
        scopeQualifier: Qualifier,
    ): InstanceFactory<*>? {
        val className = clazz.getFullName()
        val scopeKey = scopeQualifier.toString()
        val qualifierKey = qualifier?.value ?: ""

        var entry = typedIndex[className]
        while (entry != null) {
            if (entry.scopeKey == scopeKey && entry.qualifierKey == qualifierKey) return entry.factory
            entry = entry.next
        }

        val key = indexKey(clazz, qualifier, scopeQualifier)
        val factory = _instances[key] ?: return null
        remember(className, scopeKey, qualifierKey, key, factory)
        return factory
    }

    @KoinExperimentalAPI
    internal fun <T> resolveScopeArchetypeInstance(
        qualifier: Qualifier?,
        klass: KClass<*>,
        context: ResolutionContext
    ) :T? {
        return context.scope.scopeArchetype?.let {
            context.scopeArchetype = it
            resolveInstance(qualifier,klass,it,context)
        }
    }

    internal fun <T> resolveInstance(
        qualifier: Qualifier?,
        clazz: KClass<*>,
        scopeQualifier: Qualifier,
        instanceContext: ResolutionContext,
    ): T? {
        val factory = resolveDefinition(clazz, qualifier, scopeQualifier) ?: return null
        return try {
            factory.get(instanceContext) as? T
        } catch (e: org.koin.core.error.MissingScopeValueException) {
            null
        }
    }

    @PublishedApi
    internal inline fun <reified T> scopeDeclaredInstance(
        instance: T,
        scopeQualifier: Qualifier,
        scopeID: ScopeID,
        qualifier: Qualifier? = null,
        secondaryTypes: List<KClass<*>> = emptyList(),
        allowOverride: Boolean = true,
        holdInstance : Boolean
    ) {
        val primaryType = T::class
        val indexKey = indexKey(primaryType, qualifier, scopeQualifier)
        val existingFactory = instances[indexKey] as? ScopedInstanceFactory<T>
        if (existingFactory != null) {
            existingFactory.saveValue(scopeID, instance)
        } else {
            val definitionFunction : Scope.(ParametersHolder) -> T = if (!holdInstance) ( { error("Declared definition of type '$primaryType' shouldn't be executed") } ) else ({ instance })
            val def: BeanDefinition<T> = _createDefinition(Kind.Scoped, qualifier, definitionFunction, secondaryTypes, scopeQualifier)
            val factory = ScopedInstanceFactory(def, holdInstance = holdInstance)
            val hasFactoryAllowOverride =  factory.beanDefinition.allowOverride == true
            saveMapping(allowOverride || hasFactoryAllowOverride, indexKey, factory)
            def.secondaryTypes.forEach { clazz ->
                val index = indexKey(clazz, def.qualifier, def.scopeQualifier)
                saveMapping(allowOverride || hasFactoryAllowOverride, index, factory)
            }
            factory.saveValue(scopeID, instance)
        }
    }

    @PublishedApi
    internal inline fun <reified T> declareRootInstance(
        instance: T,
        qualifier: Qualifier? = null,
        secondaryTypes: List<KClass<*>> = emptyList(),
        allowOverride: Boolean = true,
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
        val factories = _instances.values.toTypedArray()
        factories.filterIsInstance<ScopedInstanceFactory<*>>().forEach { factory -> factory.drop(scope) }
    }

    internal fun close() {
        val factories = _instances.values.toTypedArray()
        factories.forEach { factory -> factory.dropAll() }
        _instances.clear()
        KoinPlatformTools.synchronized(typedIndexLock) { typedIndex.clear() }
    }

    internal fun <T> getAll(clazz: KClass<*>, instanceContext: ResolutionContext): List<T> {
        val scopeQualifier = instanceContext.scope.scopeQualifier
        val scopeArchetype = instanceContext.scope.scopeArchetype

        // Single pass instead of filter + distinct: same predicate, same iteration order over
        // _instances.values, same identity-based dedup (InstanceFactory does not override
        // equals, so distinct() was already identity). A factory appears under several index
        // keys when it declares secondary types, hence the dedup.
        val matched = ArrayList<InstanceFactory<*>>()
        val seen = mutableSetOf<InstanceFactory<*>>()
        for (factory in _instances.values) {
            val definition = factory.beanDefinition
            if (definition.scopeQualifier != scopeQualifier && definition.scopeQualifier != scopeArchetype) continue
            if (definition.primaryType != clazz && !definition.secondaryTypes.contains(clazz)) continue
            if (seen.add(factory)) matched.add(factory)
        }

        // Resolution stays a second phase on purpose: a definition can call declare() while it
        // is being created, and mutating _instances under an in-flight iteration is not safe on
        // every platform.
        val instances = ArrayList<T>(matched.size)
        for (factory in matched) {
            val instance = factory.get(instanceContext)
            if (instance != null) {
                @Suppress("UNCHECKED_CAST")
                instances.add(instance as T)
            }
        }
        return instances
    }

    internal fun unloadModules(modules: Set<Module>) {
        modules.forEach { unloadModule(it) }
    }

    private fun unloadModule(module: Module) {
        module.mappings.keys.forEach { mapping ->
            val factory = _instances[mapping]
            factory?.dropAll()
            _instances.remove(mapping)
            if (factory != null) forget(factory)
        }
    }

    fun size(): Int {
        return _instances.size
    }


}
