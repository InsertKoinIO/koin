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
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.StringQualifier
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

@PublishedApi
internal fun <K> multibindingValueQualifier(multibindingQualifier: Qualifier, key: K): Qualifier =
    StringQualifier("${multibindingQualifier.value}_of_$key")

@PublishedApi
internal fun <K> multibindingIterateKeyQualifier(
    multibindingQualifier: Qualifier,
    key: K
): Qualifier =
    StringQualifier("${multibindingQualifier.value}_iterate_$key")

@PublishedApi
internal class MultibindingIterateKey<T>(val key: T, val valueQualifier: Qualifier)

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
                    if (multibindingIterateKey.valueQualifier.value.startsWith(qualifier.value)) {
                        multibindingIterateKey.key as? K
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
        return scope.getOrNull(valueClass, multibindingValueQualifier(qualifier, key)) {
            parametersHolder
        }
    }

    override fun isEmpty(): Boolean = keys.isEmpty()

    private class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>
}

@PublishedApi
internal class SetMultibinding<E>(
    createdAtStart: Boolean,
    private val scope: Scope,
    private val qualifier: Qualifier,
    private val valueClass: KClass<*>,
    private val parametersHolder: ParametersHolder,
) : Set<E> {
    init {
        if (createdAtStart) {
            getAll()
        }
    }

    override val size: Int
        get() = getAllKeys().size

    override fun contains(element: E): Boolean {
        return getAll().contains(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return getAll().containsAll(elements)
    }

    override fun isEmpty(): Boolean = size == 0

    override fun iterator(): Iterator<E> {
        return getAll().iterator()
    }

    private fun getAllKeys(): Set<Key> {
        val multibindingKeys = mutableSetOf<Key>()
        scope.getAll<MultibindingIterateKey<*>>()
            .mapNotNullTo(multibindingKeys) { multibindingIterateKey ->
                if (multibindingIterateKey.valueQualifier.value.startsWith(qualifier.value)) {
                    multibindingIterateKey.key as? Key
                } else {
                    null
                }
            }
        return multibindingKeys
    }

    private fun getAll(): Set<E> {
        val result = LinkedHashSet<E>()
        getAllKeys().mapNotNullTo(result) { get(it) ?: return@mapNotNullTo null }
        return result
    }

    private fun get(key: Key): E? {
        return scope.getOrNull(valueClass, multibindingValueQualifier(qualifier, key)) {
            parametersHolder
        }
    }

    class Key(private val placeholder: Int) {
        override fun toString(): String = "placeholder_$placeholder"
    }

    companion object {
        private val accumulatingKey = AtomicInt(0)

        fun getDistinctKey() = Key(accumulatingKey.incrementAndGet())
    }
}
