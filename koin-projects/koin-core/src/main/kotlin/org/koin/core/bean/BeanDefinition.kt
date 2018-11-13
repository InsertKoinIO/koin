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
package org.koin.core.bean

import org.koin.core.instance.FactoryInstance
import org.koin.core.instance.Instance
import org.koin.core.instance.ScopeInstance
import org.koin.core.instance.SingleInstance
import org.koin.core.parameter.ParametersHolder
import org.koin.core.scope.setScopeId
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
data class BeanDefinition<T>(
    val name: String? = null,
    val primaryType: KClass<*>
) {
    var secondaryTypes = arrayListOf<KClass<*>>()
    lateinit var instance: Instance<T>
    lateinit var definition: Definition<T>
    var options = Options()
    var attributes = Attributes()
    lateinit var kind: Kind

    /**
     * Tells if the definition is this Kind
     */
    private fun isKind(kind: Kind): Boolean = this.kind == kind

    /**
     * Is a Scope definition
     */
    fun isScoped() = isKind(Kind.Scope)

    /**
     * Create the associated Instance Holder
     */
    fun createInstanceHolder() {
        this.instance = when (kind) {
            Kind.Single -> SingleInstance(this)
            Kind.Scope -> ScopeInstance(this)
            Kind.Factory -> FactoryInstance(this)
        }
    }

    override fun toString(): String {
        val defKind = kind.toString()
        val defName = name?.let { "name:'$name', " } ?: ""
        val defType = "type:'${primaryType.getFullName()}'"
        val defOtherTypes = if (secondaryTypes.isNotEmpty()) {
            val typesAsString = secondaryTypes.joinToString(",") { it.getFullName() }
            ", types:$typesAsString"
        } else ""
        return "$defKind[$defName$defType$defOtherTypes]"
    }

    companion object {
        inline fun <reified T> createSingle(
            name: String? = null,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            return createDefinition(name, definition)
        }

        inline fun <reified T> createScope(
            name: String? = null,
            scopeId: String,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            val beanDefinition = createDefinition(name, definition, Kind.Scope)
            beanDefinition.setScopeId(scopeId)
            beanDefinition.instance = ScopeInstance(beanDefinition)
            return beanDefinition
        }

        inline fun <reified T> createFactory(
            name: String? = null,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            return createDefinition(name, definition, Kind.Factory)
        }

        inline fun <reified T> createDefinition(
            name: String?,
            noinline definition: Definition<T>,
            kind: Kind = Kind.Single
        ): BeanDefinition<T> {
            val beanDefinition = BeanDefinition<T>(name, T::class)
            beanDefinition.definition = definition
            beanDefinition.kind = kind
            beanDefinition.createInstanceHolder()
            return beanDefinition
        }
    }
}

enum class Kind {
    Single, Factory, Scope
}

typealias Definition<T> = (ParametersHolder) -> T