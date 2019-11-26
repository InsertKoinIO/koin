package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.IndexKey
import org.koin.core.instance.InstanceContext
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope
import org.koin.ext.getFullName
import kotlin.reflect.KClass

class InstanceRegistry(val _koin: Koin, val _scope: Scope) {

    //TODO Lock - ConcurrentHashMap
    private val _instances = HashMap<IndexKey, InstanceFactory<*>>()
    val instances: Map<IndexKey, InstanceFactory<*>>
        get() = _instances

    internal fun create(definitions: Set<BeanDefinition<*>>) {
        definitions.forEach { definition ->
            saveInstance(
                definition.primaryKey,
                definition.instanceFactory.invoke(_koin, definition)
            )
            definition.secondaryKeys.forEach { secKey ->
                saveInstance(
                    secKey,
                    definition.instanceFactory.invoke(_koin, definition)
                )
            }
        }
    }

    //TODO Lock - ConcurrentHashMap
    private fun saveInstance(key: IndexKey, factory: InstanceFactory<*>) {
        if (_instances.contains(key)) {
            error("InstanceRegistry already contains index '$key'")
        } else {
            _instances[key] = factory
        }
    }

    internal fun <T> resolveInstance(indexKey: IndexKey, parameters: ParametersDefinition?): T? {
        return _instances[indexKey]?.get(defaultInstanceContext(parameters)) as? T
    }

    private fun defaultInstanceContext(parameters: ParametersDefinition?) =
        InstanceContext(_koin, _scope, parameters)

    internal fun close() {
        _instances.values.forEach { it.drop() }
        _instances.clear()
    }

    fun createEagerInstances() {
        instances.values.filterIsInstance<SingleInstanceFactory<*>>().forEach { instance ->
            instance.get(
                InstanceContext(_koin, _scope)
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getAll(clazz: KClass<*>): List<T> {
        val potentialKeys = instances.keys.filter { it.contains(clazz.getFullName()) }
        return potentialKeys.mapNotNull { key: String ->
            instances[key]?.get(
                defaultInstanceContext(
                    null
                )
            ) as? T
        }
    }

    fun <S> bind(
        primaryType: KClass<*>,
        secondaryType: KClass<*>,
        parameters: ParametersDefinition?
    ): S? {
        return instances.values.firstOrNull { instance ->
            instance.beanDefinition.canBind(
                primaryType,
                secondaryType
            )
        }?.get(defaultInstanceContext(parameters)) as? S
    }
}