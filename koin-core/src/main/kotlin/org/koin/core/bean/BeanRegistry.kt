package org.koin.core.bean

import org.koin.core.Koin
import org.koin.dsl.path.Path
import org.koin.dsl.definition.BeanDefinition

/**
 * Bean registry
 * gather definitions of beans & communicate with instance factory to handle instances
 *
 * @author - Arnaud GIULIANI
 */
class BeanRegistry() {

    val definitions = hashSetOf<BeanDefinition<*>>()

    /**
     * Add/Replace an existing bean
     *
     * @param def : Bean definition
     */
    fun declare(def: BeanDefinition<*>, path: Path) {
        val definition = def.copy(path = path)
        val existingBean = definitions.firstOrNull { it == definition }
        val override = existingBean != null
        existingBean?.let {
            definitions.remove(existingBean)
        }
        definitions += definition
        val kw = if (override) "override" else "declare"
        Koin.logger.log("[module] $kw $definition")
    }

    /**
     * Search bean by its name, respectfully to requested type.
     */
    fun searchByName(name: String, clazz: kotlin.reflect.KClass<*>): List<BeanDefinition<*>> =
        searchDefinition { it.name == name && (it.clazz == clazz || it.types.contains(clazz)) }

    /**
     * Search for any bean definition
     */
    fun searchAll(clazz: kotlin.reflect.KClass<*>): List<BeanDefinition<*>> {
        val concreteTypes = searchDefinition { it.clazz == clazz }
        val extraBindTypes = searchDefinition { it.types.contains(clazz) }
        return (concreteTypes + extraBindTypes)
    }

    /**
     * Search definition with given filter function
     */
    private fun searchDefinition(filter: (BeanDefinition<*>) -> Boolean): List<BeanDefinition<*>> =
        definitions.filter(filter)

    /**
     * Get bean definitions from given path
     */
    fun getDefinitions(paths: Set<Path>): List<BeanDefinition<*>> {
        return definitions.filter { def -> definitions.first { it == def }.path in paths }
    }

    /**
     * Clear resources
     */
    fun clear() {
        definitions.clear()
    }

}