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
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.instance.ScopedInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.parameter.ParametersHolder
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

private class MultibindingIterateKey<T>(val elementKey: T, val multibindingQualifier: Qualifier)

class MapMultibindingElementDefinition<K, E : Any> @PublishedApi internal constructor(
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
        singleOrScopedInstance(elementQualifier, elementClass, definition)
    }

    private fun declareIterateKey(key: K) {
        val iterateKeyQualifier = multibindingIterateKeyQualifier(multibindingQualifier, key)
        singleOrScopedInstance(iterateKeyQualifier, MultibindingIterateKey::class) {
            MultibindingIterateKey(key, multibindingQualifier)
        }
    }

    @OptIn(KoinInternalApi::class)
    private fun <T> singleOrScopedInstance(
        qualifier: Qualifier,
        instanceClass: KClass<*>,
        definition: Definition<T>
    ) {
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
        }
        declareModule.indexPrimaryType(instanceFactory)
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
            val multibindingKeys = mutableSetOf<K>()
            scope.getAll<MultibindingIterateKey<*>>()
                .mapNotNullTo(multibindingKeys) { multibindingIterateKey ->
                    if (multibindingIterateKey.multibindingQualifier == qualifier) {
                        multibindingIterateKey.elementKey as? K
                    } else {
                        null
                    }
                }
            return multibindingKeys
        }

    override val size: Int
        get() = keys.size

    override val values: Collection<V>
        get() = keys.mapNotNull { get(it) }

    override val entries: Set<Map.Entry<K, V>>
        get() {
            val filed = LinkedHashSet<Map.Entry<K, V>>()
            keys.mapNotNullTo(filed) { Entry(it, get(it) ?: return@mapNotNullTo null) }
            return filed
        }

    override fun containsKey(key: K): Boolean =
        keys.contains(key)

    override fun containsValue(value: V): Boolean =
        values.contains(value)

    override fun get(key: K): V? {
        return scope.getOrNull(valueClass, multibindingElementQualifier(qualifier, key)) {
            parametersHolder
        }
    }

    override fun isEmpty(): Boolean = keys.isEmpty()

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
            val sortedKeys = mapMultibinding.keys.sorted()
            val set = LinkedHashSet<E>(sortedKeys.size)
            for (key in sortedKeys) {
                val element = mapMultibinding[key] ?: continue
                if (set.add(element)) {
                    // done
                } else {
                    // latter element survives
                    set.remove(element)
                    set.add(element)
                }
            }
            return set
        }

    override val size: Int
        get() = elementSet.size

    override fun contains(element: E): Boolean =
        elementSet.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean =
        elementSet.containsAll(elements)

    override fun isEmpty(): Boolean = elementSet.isEmpty()

    override fun iterator(): Iterator<E> = elementSet.iterator()

    class Key(private val placeholder: Int) : Comparable<Key> {
        override fun toString(): String = "placeholder_$placeholder"

        override fun compareTo(other: Key): Int = placeholder.compareTo(other.placeholder)
    }

    companion object {
        private val accumulatingKey = AtomicInt(0)

        fun getDistinctKey() = Key(accumulatingKey.incrementAndGet())
    }
}
