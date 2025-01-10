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
package org.koin.core.definition

import org.koin.core.module.KoinDslMarker
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.registry.ScopeRegistry.Companion.rootScopeQualifier
import org.koin.core.scope.Scope
import org.koin.ext.getFullName
import kotlin.reflect.KClass

/**
 * Koin bean definition
 * main structure to make definition in Koin
 *
 * @author Arnaud Giuliani
 */
@KoinDslMarker
class BeanDefinition<T>(
    val scopeQualifier: Qualifier,
    val primaryType: KClass<*>,
    var qualifier: Qualifier? = null,
    val definition: Definition<T>,
    val kind: Kind,
    var secondaryTypes: List<KClass<*>> = emptyList(),
) {
    var callbacks: Callbacks<T> = Callbacks()

    @PublishedApi
    internal var _createdAtStart = false

    override fun toString(): String {
        return buildString {
            append('[')
            append(kind)
            append(": '")
            append(primaryType.getFullName())
            append('\'')
            if (qualifier != null) {
                append(",qualifier:")
                append(qualifier)
            }
            if (scopeQualifier != rootScopeQualifier) {
                append(",scope:")
                append(scopeQualifier)
            }
            if (secondaryTypes.isNotEmpty()) {
                append(",binds:")
                secondaryTypes.joinTo(this, ",") { it.getFullName() }
            }
            append(']')
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as BeanDefinition<*>

        if (primaryType != other.primaryType) return false
        if (qualifier != other.qualifier) return false
        if (scopeQualifier != other.scopeQualifier) return false

        return true
    }

    inline fun hasType(clazz: KClass<*>): Boolean {
        return primaryType == clazz || secondaryTypes.contains(clazz)
    }

    inline fun `is`(clazz: KClass<*>, qualifier: Qualifier?, scopeDefinition: Qualifier): Boolean {
        return hasType(clazz) && this.qualifier == qualifier && this.scopeQualifier == scopeDefinition
    }

    override fun hashCode(): Int {
        var result = qualifier?.hashCode() ?: 0
        result = 31 * result + primaryType.hashCode()
        result = 31 * result + scopeQualifier.hashCode()
        return result
    }
}

inline fun indexKey(clazz: KClass<*>, typeQualifier: Qualifier?, scopeQualifier: Qualifier): String {
    return buildString {
        append(clazz.getFullName())
        append(':')
        append(typeQualifier?.value ?: "")
        append(':')
        append(scopeQualifier)
    }
}

enum class Kind {
    Singleton, Factory, Scoped
}

typealias IndexKey = String
typealias Definition<T> = Scope.(ParametersHolder) -> T

inline fun <reified T> _createDefinition(
    kind: Kind = Kind.Singleton,
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>,
    secondaryTypes: List<KClass<*>> = emptyList(),
    scopeQualifier: Qualifier,
): BeanDefinition<T> {
    return BeanDefinition(
        scopeQualifier,
        T::class,
        qualifier,
        definition,
        kind,
        secondaryTypes = secondaryTypes,
    )
}

inline fun <reified T> _createDeclaredDefinition(
    kind: Kind = Kind.Singleton,
    qualifier: Qualifier? = null,
    secondaryTypes: List<KClass<*>> = emptyList(),
    scopeQualifier: Qualifier,
): BeanDefinition<T> {
    return BeanDefinition(
        scopeQualifier,
        T::class,
        qualifier,
        { error("declared instance error ") },
        kind,
        secondaryTypes = secondaryTypes,
    )
}
