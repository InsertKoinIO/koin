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
package org.koin.dsl.definition

import org.koin.core.parameter.ParameterList
import org.koin.dsl.path.Path
import org.koin.error.DefinitionBindingException
import org.koin.ext.name
import kotlin.reflect.KClass

/**
 * Bean definition
 * @author - Arnaud GIULIANI
 *
 * Gather type of T
 * defined by lazy/function
 * has a type (clazz)
 * has a BeanType : default singleton
 * has a canonicalName, if specified
 *
 * @param name - bean canonicalName
 * @param primaryType - bean class
 * @param kind - bean definition Kind
 * @param types - list of assignable types
 * @param isEager - definition tagged to be created on start
 * @param allowOverride - definition tagged to allow definition override or not
 * @param definition - bean definition function
 */
data class BeanDefinition<out T>(
    val name: String = "",
    val primaryType: KClass<*>,
    var types: List<KClass<*>> = arrayListOf(),
    val path: Path = Path.root(),
    val kind: Kind = Kind.Single,
    val isEager: Boolean = false,
    val allowOverride: Boolean = false,
    val attributes: HashMap<String, Any> = HashMap(),
    val definition: Definition<T>
) {
    internal val primaryTypeName: String = primaryType.name()
    internal val classes: List<KClass<*>> = listOf(primaryType) + types

    /**
     * Add a compatible type to current bounded definition
     */
    infix fun bind(clazz: KClass<*>): BeanDefinition<*> {
        if (!clazz.java.isAssignableFrom(this.primaryType.java)) {
            throw DefinitionBindingException("Can't bind type '$clazz' for definition $this")
        } else {
            types += clazz
        }
        return this
    }

    /**
     * Check visibility if "this" can see "other"
     */
    fun isVisible(other: BeanDefinition<*>) = other.path.isVisible(this.path)

    override fun toString(): String {
        val beanName = if (name.isEmpty()) "" else "name='$name',"
        val clazz = "class='${primaryType.java.canonicalName}'"
        val type = "$kind"
        val binds = if (types.isEmpty()) "" else ", binds~${boundTypes()}"
        val path = if (path != Path.root()) ", path:'$path'" else ""
        return "$type [$beanName$clazz$binds$path]"
    }

    private fun boundTypes(): String =
        "(" + types.joinToString { it.java.canonicalName } + ")"

    override fun equals(other: Any?): Boolean {
        return if (other is BeanDefinition<*>) {
            name == other.name &&
                    primaryType == other.primaryType &&
                    path == other.path &&
                    attributes == other.attributes
        } else false
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + primaryTypeName.hashCode()
        result = 31 * result + attributes.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }
}

/**
 * Type Definition function - what's loadModules a given component T
 */
typealias Definition<T> = (ParameterList) -> T

/**
 * Bean definition Kind
 * - single
 * - factory
 * - scope
 */
enum class Kind {
    Single, Factory, Scope
}