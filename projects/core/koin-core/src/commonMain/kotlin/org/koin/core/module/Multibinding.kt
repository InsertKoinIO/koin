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
package org.koin.core.module

import co.touchlab.stately.concurrency.AtomicInt
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.definition.indexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ScopedInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.instance.keepDefinitionOrderAcrossModules
import org.koin.core.parameter.ParametersHolder
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.StringQualifier
import org.koin.core.registry.ScopeRegistry.Companion.rootScopeQualifier
import org.koin.core.scope.Scope
import org.koin.ext.getFullName
import kotlin.reflect.KClass

/**
 * Multibinding of Map & Set
 *
 * @author - luozejiaqun
 */
inline fun <reified K, reified V> mapMultibindingQualifier(): Qualifier =
    StringQualifier("map_multibinding_${K::class.getFullName()}_${V::class.getFullName()}")

inline fun <reified E> setMultibindingQualifier(): Qualifier =
    StringQualifier("set_multibinding_${E::class.getFullName()}")

private fun <K> multibindingElementQualifier(multibindingQualifier: Qualifier, key: K): Qualifier =
    StringQualifier("${multibindingQualifier.value}_of_$key")

private fun <K> multibindingIterateKeyQualifier(
    multibindingQualifier: Qualifier,
    key: K
): Qualifier =
    StringQualifier("${multibindingQualifier.value}_iterate_$key")

class MapMultibindingKeyTypeException(msg: String) : Exception(msg)

private class MultibindingIterateKey<T>(
    val elementKey: T,
    val multibindingQualifier: Qualifier,
)

class MapMultibindingElementDefinition<K : Any, E : Any> @PublishedApi internal constructor(
    private val multibindingQualifier: Qualifier,
    private val elementClass: KClass<E>,
    private val declareModule: Module,
    private val scopeQualifier: Qualifier,
) {
    private val isRootScope = scopeQualifier == rootScopeQualifier

    /**
     * the parameters of [definition] come from MapMultibinding creation
     *
     * ```
     * koin.getMapMultibinding<K, E> { parametersOf("this is the parameters you get") }
     * ```
     */
    fun intoMap(key: K, definition: Definition<E>) {
        declareElement(key, definition)
        declareIterateKey(key)
    }

    private fun declareElement(key: K, definition: Definition<E>) {
        val elementQualifier = multibindingElementQualifier(multibindingQualifier, key)
        singleOrScopedInstance(elementQualifier, elementClass, definition) {
            it.keepDefinitionOrderAcrossModules(ascending = true)
        }
    }

    private fun declareIterateKey(key: K) {
        val iterateKeyQualifier = multibindingIterateKeyQualifier(multibindingQualifier, key)
        val oldInstanceFactory =
            singleOrScopedInstance(
                iterateKeyQualifier,
                MultibindingIterateKey::class,
                definition = {
                    MultibindingIterateKey(key, multibindingQualifier)
                },
                instanceFactoryModifier = {
                    it.keepDefinitionOrderAcrossModules(ascending = false)
                })
        checkMultibindingKeyCollision(oldInstanceFactory, key)
    }

    /**
     * @return old definition
     */
    @OptIn(KoinInternalApi::class)
    private inline fun <T> singleOrScopedInstance(
        qualifier: Qualifier,
        instanceClass: KClass<*>,
        noinline definition: Definition<T>,
        instanceFactoryModifier: (InstanceFactory<*>) -> InstanceFactory<*>
    ): InstanceFactory<*>? {
        val instanceFactory = if (isRootScope) {
            SingleInstanceFactory(
                BeanDefinition(
                    scopeQualifier,
                    instanceClass,
                    qualifier,
                    definition,
                    Kind.Singleton,
                )
            )
        } else {
            ScopedInstanceFactory(
                BeanDefinition(
                    scopeQualifier,
                    instanceClass,
                    qualifier,
                    definition,
                    Kind.Scoped,
                )
            )
        }.let(instanceFactoryModifier)
        return indexPrimaryType(instanceFactory)
    }

    @OptIn(KoinInternalApi::class)
    private fun indexPrimaryType(instanceFactory: InstanceFactory<*>): InstanceFactory<*>? {
        val def = instanceFactory.beanDefinition
        val mapping = indexKey(def.primaryType, def.qualifier, def.scopeQualifier)
        return declareModule.mappings[mapping].apply {
            declareModule.saveMapping(mapping, instanceFactory)
        }
    }

    // TODO this only works for the same module, find a way to check across modules.
    private fun checkMultibindingKeyCollision(oldInstanceFactory: InstanceFactory<*>?, newKey: K) {
        if (oldInstanceFactory != null && needToCheckKeyType(newKey)) {
            val oldKey = (oldInstanceFactory.beanDefinition.definition(
                Scope(scopeQualifier, "stub scope for multibinding", _koin = Koin()),
                emptyParametersHolder()
            ) as? MultibindingIterateKey<*>)?.elementKey
            if (newKey != oldKey) {
                throw MapMultibindingKeyTypeException(
                    """
                        MapMultibinding key collision: "$newKey" conflicts with a previous key. But it does NOT equal to the previous key.
                        Consider overriding `toString()` correctly for the class of "$newKey".
                    """.trimIndent()
                )
            }
        }
    }

    private fun needToCheckKeyType(key: K): Boolean {
        return when (key) {
            is Boolean,
            is Number,
            is String,
            is Enum<*>,
            is KClass<*> -> false

            else -> true
        }
    }
}

@PublishedApi
internal class MapMultibinding<K : Any, V>(
    createdAtStart: Boolean,
    private val scope: Scope,
    private val qualifier: Qualifier,
    private val valueClass: KClass<*>,
    private val parametersHolder: ParametersHolder,
) : Map<K, V> {

    init {
        if (createdAtStart) {
            values
        }
    }

    override val keys: Set<K>
        get() {
            // in the definition order
            return reversedKeys.reversed()
        }

    // this is useful for element overriding
    val reversedKeys: LinkedHashSet<K>
        get() {
            val multibindingKeys = LinkedHashSet<K>()
            // MultibindingIterateKey is created by OrderedInstanceFactory(ascending = false)
            // so the list here is in reversed order
            scope.getAll<MultibindingIterateKey<*>>()
                .mapNotNullTo(multibindingKeys) {
                    if (it.multibindingQualifier == qualifier) {
                        it.elementKey as? K
                    } else {
                        null
                    }
                }
            return multibindingKeys
        }

    override val size: Int
        get() = reversedKeys.size

    override val values: Collection<V>
        get() = keys.mapNotNull { get(it) }

    override val entries: Set<Map.Entry<K, V>>
        get() {
            val filed = LinkedHashSet<Map.Entry<K, V>>()
            keys.mapNotNullTo(filed) { Entry(it, get(it) ?: return@mapNotNullTo null) }
            return filed
        }

    override fun containsKey(key: K): Boolean =
        reversedKeys.contains(key)

    override fun containsValue(value: V): Boolean =
        values.contains(value)

    override fun get(key: K): V? {
        return scope.getOrNull<V>(valueClass, multibindingElementQualifier(qualifier, key)) {
            parametersHolder
        }.takeIf {
            // there is a case where the multibindingElementQualifier is valid, but the key is invalid
            isKeyValid(key, it)
        } ?: getFromKeys(key)
    }

    // retrieve from keys, this may happen when key1 == key2 but key1.toString() != key2.toString()
    private fun getFromKeys(key: K): V? {
        return reversedKeys.find { it == key }?.let { get(it) }
    }

    // a key is valid if it's in the key set and the value is not null
    private fun isKeyValid(key: K, value: Any?): Boolean {
        return when (key) {
            is Boolean,
            is Number,
            is String,
            is Enum<*>,
            is KClass<*>,
            is SetMultibinding.Key -> {
                // for those types of key, it's always valid if the corresponding value is not null
                value != null
            }

            else -> value != null && reversedKeys.contains(key)
        }
    }

    override fun isEmpty(): Boolean = reversedKeys.isEmpty()

    private class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>
}

class SetMultibindingElementDefinition<E : Any> @PublishedApi internal constructor(
    multibindingQualifier: Qualifier,
    elementClass: KClass<E>,
    declareModule: Module,
    scopeQualifier: Qualifier,
) {
    private val multibindingElementDefinition =
        MapMultibindingElementDefinition<SetMultibinding.Key, E>(
            multibindingQualifier,
            elementClass,
            declareModule,
            scopeQualifier
        )

    /**
     * the parameters of [definition] come from SetMultibinding creation
     *
     * ```
     * koin.getSetMultibinding<E> { parametersOf("this is the parameters you get") }
     * ```
     */
    fun intoSet(definition: Definition<E>) {
        val key = SetMultibinding.getDistinctKey()
        multibindingElementDefinition.intoMap(key, definition)
    }
}

@PublishedApi
internal class SetMultibinding<E>(
    createdAtStart: Boolean,
    scope: Scope,
    qualifier: Qualifier,
    elementClass: KClass<*>,
    parametersHolder: ParametersHolder,
) : Set<E> {
    private val mapMultibinding = MapMultibinding<Key, E>(
        false,
        scope,
        qualifier,
        elementClass,
        parametersHolder
    )

    init {
        if (createdAtStart) {
            elementSet
        }
    }

    private val elementSet: Set<E>
        get() {
            val reversedKeys = mapMultibinding.reversedKeys
            val elements = LinkedHashSet<E>(reversedKeys.size)
            for (key in reversedKeys) {
                val element = mapMultibinding[key] ?: continue
                elements.add(element)
            }
            return elements.reversed()
        }

    override val size: Int
        get() = elementSet.size

    override fun contains(element: E): Boolean =
        elementSet.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean =
        elementSet.containsAll(elements)

    override fun isEmpty(): Boolean = elementSet.isEmpty()

    override fun iterator(): Iterator<E> = elementSet.iterator()

    class Key(private val placeholder: Int) {
        override fun toString(): String = "placeholder_$placeholder"
    }

    companion object {
        private val accumulatingKey = AtomicInt(0)

        fun getDistinctKey() = Key(accumulatingKey.incrementAndGet())
    }
}

private fun <E> LinkedHashSet<E>.reversed(): LinkedHashSet<E> {
    if (this.size <= 1) return this
    val elements = this.toList()
    this.clear()
    elements.reversed().forEach(this::add)
    return this
}
