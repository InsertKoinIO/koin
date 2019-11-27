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

import org.koin.core.Koin
import org.koin.core.instance.InstanceFactory
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeDefinition
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
data class BeanDefinition<T>(
    val scopeDefinition: ScopeDefinition,
    val primaryType: KClass<*>,
    val qualifier: Qualifier? = null,
    val instanceFactory: InstanceFactoryBuilder,
    val definition: Definition<T>,
    val kind: Kind,
    val secondaryTypes: List<KClass<*>> = listOf(),
    val options: Options = Options(),
    val properties: Properties = Properties(),
    val callbacks: Callbacks<T> = Callbacks()
) {

    override fun toString(): String {
        val defKind = kind.toString()
        val defType = "'${primaryType.getFullName()}'"
        val defName = qualifier?.let { ",qualifier:$qualifier" } ?: ""
        val defScope =
            scopeDefinition.let { if (it.isRoot) "" else ",scope:${scopeDefinition.qualifier}" }
        val defOtherTypes = if (secondaryTypes.isNotEmpty()) {
            val typesAsString = secondaryTypes.joinToString(",") { it.getFullName() }
            ",binds:$typesAsString"
        } else ""
        return "[$defKind:$defType$defName$defScope$defOtherTypes]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as BeanDefinition<*>

        if (primaryType != other.primaryType) return false
        if (qualifier != other.qualifier) return false
        if (scopeDefinition != other.scopeDefinition) return false

        return true
    }

    fun hasType(clazz: KClass<*>): Boolean {
        return primaryType == clazz || secondaryTypes.contains(clazz)
    }

    fun `is`(clazz: KClass<*>, qualifier: Qualifier?, scopeDefinition: ScopeDefinition): Boolean {
        return hasType(clazz) && this.qualifier == qualifier && this.scopeDefinition == scopeDefinition
    }

    fun canBind(primary: KClass<*>, secondary: KClass<*>): Boolean {
        return primaryType == primary && secondaryTypes.contains(secondary)
    }

    override fun hashCode(): Int {
        var result = qualifier?.hashCode() ?: 0
        result = 31 * result + primaryType.hashCode()
        result = 31 * result + scopeDefinition.hashCode()
        return result
    }

}

fun indexKey(clazz: KClass<*>, qualifier: Qualifier?): String {
    return if (qualifier?.value != null) {
        "${clazz.getFullName()}::${qualifier.value}"
    } else clazz.getFullName()
}

enum class Kind {
    Single, Factory
}

typealias IndexKey = String
typealias Definition<T> = Scope.(DefinitionParameters) -> T
typealias InstanceFactoryBuilder = (Koin, BeanDefinition<*>) -> InstanceFactory<*>