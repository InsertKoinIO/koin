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
 * @param clazz - bean class
 * @param isSingleton - is the bean a singleton
 * @param types - list of assignable types
 * @param isEager - definition tagged to be created on start
 * @param allowOverride - definition tagged to allow definition override or not
 * @param definition - bean definition function
 */
data class BeanDefinition<out T>(
    val name: String = "",
    val clazz: KClass<*>,
    var types: List<KClass<*>> = arrayListOf(),
    val path: Path = Path.root(),
    val isSingleton: Boolean = true,
    val isEager: Boolean = false,
    val allowOverride: Boolean = false,
    val definition: Definition<T>
) {

    // Available classes to match
    internal val classes : List<String> = listOf<String>(clazz.java.canonicalName) + types.map { it.java.canonicalName }

    /**
     * Add a compatible type to current bounded definition
     */
    infix fun bind(clazz: KClass<*>): BeanDefinition<*> {
        if (!clazz.java.isAssignableFrom(this.clazz.java)) {
            throw DefinitionBindingException("Can't bind type '$clazz' for definition $this")
        } else {
            types += clazz
        }
        return this
    }

    /**
     * Bean definition is not a singleton, but a factory
     */
    fun isNotASingleton(): Boolean = !isSingleton

    private fun boundTypes(): String =
        "(" + types.map { it.java.canonicalName }.joinToString() + ")"

    /**
     * Check visibility if "this" can see "other"
     */
    fun canSee(other: BeanDefinition<*>) = other.path.isVisible(this.path)

    override fun toString(): String {
        val beanName = if (name.isEmpty()) "" else "canonicalName='$name',"
        val clazz = "class='${clazz.java.canonicalName}'"
        val type = if (isSingleton) "Single" else "Factory"
        val binds = if (types.isEmpty()) "" else ", binds~${boundTypes()}"
        val path = if (path != Path.root()) ", path:'$path'" else ""
        return "$type [$beanName$clazz$binds$path]"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is BeanDefinition<*>) {
            name == other.name &&
                    clazz == other.clazz &&
                    path == other.path &&
                    types == other.types
        } else false
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + clazz.hashCode()
        result = 31 * result + types.hashCode()
        result = 31 * result + path.hashCode()
        return result
    }
}

/**
 * Type Definition function - what's build a given component T
 */
typealias Definition<T> = (ParameterList) -> T