package org.koin.dsl.definition

import org.koin.core.parameter.ParameterList
import org.koin.dsl.path.Path
import kotlin.reflect.KClass

/**
 * Bean definition
 * @author - Arnaud GIULIANI
 * @author - Laurent BARESSE
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
    val path: Path = Path.root(),
    val isSingleton: Boolean = true,
    val isEager: Boolean = false,
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
    fun isNotASingleton(): Boolean = !isSingleton

    private fun boundTypes(): String = "(" + types.map { it.java.canonicalName }.joinToString() + ")"

    /**
     * Check visibility if "this" can see "other"
     */
    fun canSee(other: BeanDefinition<*>) = other.path.isVisible(this.path)

    override fun toString(): String {
        val beanName = if (name.isEmpty()) "" else "name='$name',"
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
                    types == other.types &&
                    isSingleton == other.isSingleton
        } else false
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + clazz.hashCode()
        result = 31 * result + types.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + isSingleton.hashCode()
        result = 31 * result + isEager.hashCode()
        return result
    }
}

/**
 * Type Definition function - what's build a given component T
 */
typealias Definition<T> = (ParameterList) -> T