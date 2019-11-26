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
        val defName = qualifier?.let { "name:'$qualifier', " } ?: ""
        val defScope = scopeDefinition.let { "scope:'${scopeDefinition.qualifier}', " }
        val defType = "primary_type:'${primaryType.getFullName()}'"
        val defOtherTypes = if (secondaryTypes.isNotEmpty()) {
            val typesAsString = secondaryTypes.joinToString(",") { it.getFullName() }
            ", secondary_type:$typesAsString"
        } else ""
        return "[type:$defKind,$defScope$defName$defType$defOtherTypes]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as BeanDefinition<*>

        if (primaryType != other.primaryType) return false
        if (qualifier != other.qualifier) return false
        if (scopeDefinition != other.scopeDefinition) return false

        return true
    }

    fun canBind(primary: KClass<*>, secondary: KClass<*>): Boolean {
        return primaryType == primary && secondaryTypes.contains(secondary)
    }

    val primaryKey: IndexKey by lazy { indexKey(primaryType, qualifier) }

    val secondaryKeys: List<IndexKey> by lazy { secondaryTypes.map { indexKey(it, qualifier) } }

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