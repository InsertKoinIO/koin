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

import org.koin.core.instance.*
import org.koin.core.parameter.DefinitionParameters
import org.koin.ext.getFullName
import kotlin.reflect.KClass

/**
 * Koin bean definition
 * main structure to make definition in Koin
 *
 * @param name
 * @param primaryType
 *
 * @author Arnaud Giuliani
 */
class BeanDefinition<T>(
        val name: String? = null,
        val primaryType: KClass<*>
) {
    // Main data
    var secondaryTypes = arrayListOf<KClass<*>>()
    var instance: Instance<T>? = null
    lateinit var definition: Definition<T>
    var options = Options()
    var attributes = Attributes()
    lateinit var kind: Kind

    // lifecycle
    var onRelease: OnReleaseCallback<T>? = null
    var onClose: OnCloseCallback<T>? = null

    /**
     * Tells if the definition is this Kind
     */
    fun isKind(kind: Kind): Boolean = this.kind == kind

    /**
     * Is a Scope definition
     */
    fun isScoped() = isKind(Kind.Scope)

    /**
     * Create the associated Instance Holder
     */
    open fun createInstanceHolder() {
        this.instance = when (kind) {
            Kind.Single -> SingleInstance(this)
            Kind.Scope -> ScopedInstance(this)
            Kind.Factory -> FactoryInstance(this)
            else -> error("Unknown definition type: $this")
        }
    }

    /**
     * Resolve instance
     */
    fun <T> resolveInstance(context: InstanceContext) = instance?.get<T>(context)
            ?: error("Definition without any InstanceContext - $this")

    override fun toString(): String {
        val defKind = kind.toString()
        val defName = name?.let { "name:'$name', " } ?: ""
        val defType = "class:'${primaryType.getFullName()}'"
        val defOtherTypes = if (secondaryTypes.isNotEmpty()) {
            val typesAsString = secondaryTypes.joinToString(",") { it.getFullName() }
            ", classes:$typesAsString"
        } else ""
        return "[type:$defKind,$defName$defType$defOtherTypes]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BeanDefinition<*>

        if (name != other.name) return false
        if (primaryType != other.primaryType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + primaryType.hashCode()
        return result
    }

    fun close() {
        instance?.close()
        instance = null
    }
}

enum class Kind {
    Single, Factory, Scope, Other
}

typealias Definition<T> = DefinitionContext.(DefinitionParameters) -> T
typealias OnReleaseCallback<T> = (T?) -> Unit
typealias OnCloseCallback<T> = (T?) -> Unit