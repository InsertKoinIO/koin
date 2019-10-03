/*
 * Copyright 2017-2018 the original author or authors.
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

import org.koin.core.error.InstanceHolderKindMismatchException
import org.koin.core.instance.DefinitionInstance
import org.koin.core.instance.FactoryDefinitionInstance
import org.koin.core.instance.InstanceContext
import org.koin.core.instance.ScopeDefinitionInstance
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.ext.getFullName
import kotlin.reflect.KClass

/**
 * Koin bean definition
 * main structure to make definition in Koin
 *
 * @param qualifier
 * @param primaryType
 *
 * @author Arnaud Giuliani
 */
class BeanDefinition<S: Scope, T>(
        val qualifier: Qualifier? = null,
        val scopeName: Qualifier? = null,
        val primaryType: KClass<*>,
        val kind: Kind,
        val definition: Definition<S, T>
) {
    // Main data
    var secondaryTypes = arrayListOf<KClass<*>>()
    var instance: DefinitionInstance<S, T>? = null
    var options = Options()
    var properties = Properties()

    // lifecycle
    var onRelease: OnReleaseCallback<T>? = null
    var onClose: OnCloseCallback<T>? = null

    /**
     *
     */
    fun hasScopeSet() = scopeName != null

    /**
     * Create the associated Instance Holder
     */
    fun createInstanceHolder() {
        val currentInstance = instance
        if (currentInstance != null) {
            checkInstanceCreationRequest(currentInstance)
            return
        }
        instance = when (kind) {
            Kind.Factory -> FactoryDefinitionInstance(this)
            Kind.Scoped -> ScopeDefinitionInstance(this)
        }
    }

    private fun checkInstanceCreationRequest(currentInstance: DefinitionInstance<S, T>) {
        val message = "Instance holder is ${currentInstance::class} but creation requested with kind: ${kind.name}"
        if (currentInstance is FactoryDefinitionInstance && kind != Kind.Factory) {
            throw InstanceHolderKindMismatchException(message)
        } else if (currentInstance is ScopeDefinitionInstance && kind == Kind.Factory) {
            throw InstanceHolderKindMismatchException(message)
        }
    }

    /**
     * Resolve instance
     */
    fun <T> resolveInstance(context: InstanceContext) = instance?.get<T>(context)
            ?: error("Definition without any InstanceContext - $this")

    override fun toString(): String {
        val defKind = kind.toString()
        val defName = qualifier?.let { "name:'$qualifier', " } ?: ""
        val defScope = scopeName?.let { "scope:'$scopeName', " } ?: ""
        val defType = "primary_type:'${primaryType.getFullName()}'"
        val defOtherTypes = if (secondaryTypes.isNotEmpty()) {
            val typesAsString = secondaryTypes.joinToString(",") { it.getFullName() }
            ", secondary_type:$typesAsString"
        } else ""
        return "[type:$defKind,$defScope$defName$defType$defOtherTypes]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BeanDefinition<*, *>

        if (qualifier != other.qualifier) return false
        if (primaryType != other.primaryType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = qualifier?.hashCode() ?: 0
        result = 31 * result + primaryType.hashCode()
        return result
    }

    fun close() {
        instance?.close()
        instance = null
    }
}

enum class Kind {
    Factory, Scoped
}

typealias Definition<S, T> = S.(DefinitionParameters) -> T
typealias OnReleaseCallback<T> = (T?) -> Unit
typealias OnCloseCallback<T> = (T?) -> Unit