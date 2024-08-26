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
package org.koin.dsl

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.*
import org.koin.core.qualifier.Qualifier

/**
 * DSL Scope Definition
 */
@OptIn(KoinInternalApi::class)
@Suppress("UNUSED_PARAMETER")
@KoinDslMarker
class ScopeDSL(val scopeQualifier: Qualifier, val module: Module) {

    inline fun <reified T> scoped(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
    ): KoinDefinition<T> {
        val def = _scopedInstanceFactory(qualifier, definition, scopeQualifier)
        module.indexPrimaryType(def)
        return KoinDefinition(module, def)
    }

    inline fun <reified T> factory(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
    ): KoinDefinition<T> {
        return module.factory(qualifier, definition, scopeQualifier)
    }

    /**
     * Declare a scoped Map<K, V> and inject an element to it with a given key
     * @param key can't be null
     * @param definition - the element definition function
     */
    inline fun <reified K : Any, reified V> intoMap(
        key: K,
        qualifier: Qualifier = mapMultibindingQualifier<K, V>(),
        noinline definition: Definition<V>,
    ): KoinDefinition<Map<K, V>> {
        scoped(multibindingValueQualifier(qualifier, key), definition)
        scoped(multibindingIterateKeyQualifier(qualifier, key)) {
            MultibindingIterateKey(key, multibindingValueQualifier(qualifier, key))
        }
        return declareMapMultibinding(qualifier)
    }

    /**
     * Declare a scoped Map<K, V> definition, the key type [K] can't be null
     * @param qualifier can't be null
     */
    inline fun <reified K : Any, reified V> declareMapMultibinding(
        qualifier: Qualifier = mapMultibindingQualifier<K, V>(),
    ): KoinDefinition<Map<K, V>> {
        return scoped(qualifier) { parametersHolder ->
            MapMultibinding(false, this, qualifier, V::class, parametersHolder)
        }
    }

    /**
     * Declare a scoped Set<E> and inject an element to it
     * @param definition - the element definition function
     */
    inline fun <reified E> intoSet(
        qualifier: Qualifier = setMultibindingQualifier<E>(),
        noinline definition: Definition<E>,
    ): KoinDefinition<Set<E>> {
        val key = SetMultibinding.getDistinctKey()
        scoped(multibindingValueQualifier(qualifier, key), definition = definition)
        scoped(multibindingIterateKeyQualifier(qualifier, key)) {
            MultibindingIterateKey(key, multibindingValueQualifier(qualifier, key))
        }
        return declareSetMultibinding(qualifier)
    }

    /**
     * Declare a scoped Set<E> definition
     * @param qualifier can't be null
     */
    inline fun <reified E> declareSetMultibinding(
        qualifier: Qualifier = setMultibindingQualifier<E>(),
    ): KoinDefinition<Set<E>> {
        return scoped(qualifier) { parametersHolder ->
            SetMultibinding(false, this, qualifier, E::class, parametersHolder)
        }
    }
}
