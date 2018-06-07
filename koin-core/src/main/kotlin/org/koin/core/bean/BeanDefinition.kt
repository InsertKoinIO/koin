package org.koin.core.bean

import org.koin.core.scope.Scope
import org.koin.dsl.context.ParameterProvider
import kotlin.reflect.KClass

/**
 * Bean definition
 * @author - Arnaud GIULIANI
 *
 * Gather type of T
 * defined by lazy/function
 * has a type (clazz)
 * has a BeanType : default singleton
 * has a name, if specified
 *
 * @param name - bean name
 * @param clazz - bean class
 * @param isSingleton - is the bean a singleton
 * @param types - list of assignable types
 * @param definition - bean definition function
 */
data class BeanDefinition<out T>(
    val name: String = "",
    val clazz: KClass<*>,
    var types: List<KClass<*>> = arrayListOf(),
    val scope: Scope = Scope.root(),
    val isSingleton: Boolean = true,
    val override: Boolean = false,
    val definition: Definition<T>
) {

    /**
     * Add a compatible type to current bounded definition
     */
    infix fun bind(clazz: KClass<*>): BeanDefinition<*> {
        types += clazz
        return this
    }

    /**
     * Bean definition is not a singleton, but a factory
     */
    fun isNotASingleton() = !isSingleton

    private fun boundTypes(): String = "(" + types.joinToString { it.java.canonicalName } + ")"

    override fun toString(): String {
        val n = if (name.isBlank()) "" else "name='$name', "
        val c = "class=${clazz.java.canonicalName}"
        val s = if (isSingleton) "Bean" else "Factory"
        val b = if (types.isEmpty()) "" else ", binds~${boundTypes()}"
        val sn = if (scope != Scope.root()) ", scope:${scope.name}" else ""
        val o = ", override=$override"
        return "$s[$n$c$b$sn$o]"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is BeanDefinition<*>) {
            name == other.name && clazz == other.clazz && scope == other.scope
        } else false
    }

    override fun hashCode(): Int {
        return name.hashCode() + clazz.hashCode() + scope.hashCode()
    }
}

typealias Definition<T> = (ParameterProvider) -> T
