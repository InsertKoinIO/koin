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
import org.koin.core.definition.indexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ScopedInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.instance.keepDefinitionOrderAcrossModules
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier._q
import org.koin.core.registry.ScopeRegistry.Companion.rootScopeQualifier
import org.koin.core.scope.Scope
import org.koin.ext.getFullName
import org.koin.mp.KoinPlatformTools
import org.koin.mp.generateId
import kotlin.reflect.KClass

/**
 * Multibinding of Map & Set
 *
 * @author - luozejiaqun
 */
inline fun <reified K, reified V> mapMultibindingQualifier(): Qualifier =
    _q("MapMultibinding<${K::class.getFullName()}, ${V::class.getFullName()}>")

inline fun <reified E> setMultibindingQualifier(): Qualifier =
    _q("SetMultibinding<${E::class.getFullName()}>")

private fun <K> multibindingElementQualifier(
    keyClass: KClass<*>,
    multibindingQualifier: Qualifier,
    key: K
): Qualifier = distinctQualifierBasedOnType(keyClass, key) {
    _q("${multibindingQualifier.value} of $it")
}

private fun <K> multibindingIterateKeyQualifier(
    keyClass: KClass<*>,
    multibindingQualifier: Qualifier,
    key: K
): Qualifier = distinctQualifierBasedOnType(keyClass, key) {
    _q("${multibindingQualifier.value} iterate of $it")
}

private fun <K> distinctQualifierBasedOnType(
    type: KClass<*>,
    value: K,
    multibindingQualifierMixin: (QualifierValue) -> Qualifier
): Qualifier = when (type) {
    Boolean::class,
    Byte::class,
    Int::class,
    Long::class,
    Float::class,
    Double::class,
    String::class,
    KClass::class,
    SetMultibinding.Key::class -> multibindingQualifierMixin(value.toString())

    Enum::class -> multibindingQualifierMixin((value as Enum<*>).name)

    else -> _q(KoinPlatformTools.generateId())
}

internal data class MultibindingIterateKey<T>(
    val elementKey: T,
    val multibindingQualifier: Qualifier,
) {
    var elementQualifier: Qualifier = _q("")

    internal constructor(
        elementKey: T,
        multibindingQualifier: Qualifier,
        elementQualifier: Qualifier,
    ) : this(
        elementKey,
        multibindingQualifier,
    ) {
        this.elementQualifier = elementQualifier
    }
}

class MapMultibindingElementDefinition<in K : Any, in E : Any> @PublishedApi internal constructor(
    private val multibindingQualifier: Qualifier,
    private val keyClass: KClass<K>,
    private val elementClass: KClass<E>,
    private val declareModule: Module,
    private val scopeQualifier: Qualifier,
) {
    private val isRootScope = scopeQualifier == rootScopeQualifier

    fun intoMap(key: K, element: E) {
        intoMap(key) { element }
    }

    /**
     * the parameters of [definition] come from MapMultibinding creation
     *
     * ```
     * koin.getMapMultibinding<K, E> { parametersOf("this is the parameters you get") }
     * ```
     */
    fun intoMap(key: K, definition: Definition<E>) {
        val elementQualifier = declareElement(key, definition)
        declareIterateKey(key, elementQualifier)
    }

    private fun declareElement(key: K, definition: Definition<E>): Qualifier {
        val elementQualifier = multibindingElementQualifier(keyClass, multibindingQualifier, key)
        singleOrScopedInstance(elementQualifier, elementClass, definition) {
            it.keepDefinitionOrderAcrossModules(ascending = true)
        }
        return elementQualifier
    }

    private fun declareIterateKey(key: K, elementQualifier: Qualifier) {
        val iterateKeyQualifier =
            multibindingIterateKeyQualifier(keyClass, multibindingQualifier, key)
        singleOrScopedInstance(
            iterateKeyQualifier,
            MultibindingIterateKey::class,
            definition = {
                MultibindingIterateKey(key, multibindingQualifier, elementQualifier)
            },
            instanceFactoryModifier = {
                it.keepDefinitionOrderAcrossModules(ascending = false)
            })
    }

    @OptIn(KoinInternalApi::class)
    private inline fun <T> singleOrScopedInstance(
        qualifier: Qualifier,
        instanceClass: KClass<*>,
        noinline definition: Definition<T>,
        instanceFactoryModifier: (InstanceFactory<*>) -> InstanceFactory<*>
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
        }.let(instanceFactoryModifier)
        indexPrimaryType(instanceFactory)
    }

    @OptIn(KoinInternalApi::class)
    private fun indexPrimaryType(instanceFactory: InstanceFactory<*>) {
        val def = instanceFactory.beanDefinition
        val mapping = indexKey(def.primaryType, def.qualifier, def.scopeQualifier)
        declareModule.saveMapping(mapping, instanceFactory)
    }
}

@PublishedApi
internal class MapMultibinding<K : Any, V>(
    createdAtStart: Boolean,
    private val scope: Scope,
    private val qualifier: Qualifier,
    private val keyClass: KClass<*>,
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
            return reversedKeys.reversed { it.elementKey }
        }

    // this is useful for element overriding
    internal val reversedKeys: LinkedHashSet<MultibindingIterateKey<K>>
        get() {
            val multibindingKeys = LinkedHashSet<MultibindingIterateKey<K>>()
            // MultibindingIterateKey is created by OrderedInstanceFactory(ascending = false)
            // so the list here is in reversed order
            scope.getAll<MultibindingIterateKey<K>>(MultibindingIterateKey::class)
                .mapNotNullTo(multibindingKeys) {
                    if (it.multibindingQualifier == qualifier) {
                        it
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
        reversedKeys.contains(MultibindingIterateKey(key, qualifier))

    override fun containsValue(value: V): Boolean =
        values.contains(value)

    override fun get(key: K): V? {
        return getOrNull(multibindingElementQualifier(keyClass, qualifier, key)) ?: getFromKeys(key)
    }

    private fun getOrNull(elementQualifier: Qualifier): V? {
        return scope.getOrNull<V>(valueClass, elementQualifier) {
            parametersHolder
        }
    }

    // retrieve from keys, this may happen when key1 == key2
    // but multibindingElementQualifier(key1) != multibindingElementQualifier(key2)
    private fun getFromKeys(key: K): V? {
        return reversedKeys.find { it.elementKey == key }?.let { getOrNull(it.elementQualifier) }
    }

    override fun isEmpty(): Boolean = reversedKeys.isEmpty()

    private class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>
}

class SetMultibindingElementDefinition<in E : Any> @PublishedApi internal constructor(
    multibindingQualifier: Qualifier,
    elementClass: KClass<E>,
    declareModule: Module,
    scopeQualifier: Qualifier,
) {
    private val multibindingElementDefinition =
        MapMultibindingElementDefinition<SetMultibinding.Key, E>(
            multibindingQualifier,
            SetMultibinding.Key::class,
            elementClass,
            declareModule,
            scopeQualifier
        )

    fun intoSet(element: E) {
        intoSet { element }
    }

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
        Key::class,
        elementClass,
        parametersHolder
    )

    init {
        if (createdAtStart) {
            getElementSet()
        }
    }

    private fun getElementSet(ordered: Boolean = false): Set<E> {
        val reversedKeys = mapMultibinding.reversedKeys
        val elements = LinkedHashSet<E>(reversedKeys.size)
        for (iterateKey in reversedKeys) {
            val element = mapMultibinding[iterateKey.elementKey] ?: continue
            elements.add(element)
        }
        return if (ordered) elements.reversed { it } else elements
    }

    override val size: Int
        get() = getElementSet().size

    override fun contains(element: E): Boolean =
        getElementSet().contains(element)

    override fun containsAll(elements: Collection<E>): Boolean =
        getElementSet().containsAll(elements)

    override fun isEmpty(): Boolean = getElementSet().isEmpty()

    override fun iterator(): Iterator<E> = getElementSet(true).iterator()

    class Key(private val placeholder: Int) {
        override fun toString(): String = "placeholder$placeholder"
    }

    companion object {
        private val accumulatingKey = AtomicInt(0)

        fun getDistinctKey() = Key(accumulatingKey.incrementAndGet())
    }
}

private inline fun <E, R> LinkedHashSet<E>.reversed(transform: (E) -> R): Set<R> {
    val set = LinkedHashSet<R>(this.size)
    toList<E>().reversed().forEach {
        set.add(transform(it))
    }
    return set
}
