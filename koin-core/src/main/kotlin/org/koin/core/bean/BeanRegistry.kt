package org.koin.core.bean

import org.koin.Koin
import org.koin.core.scope.Scope
import org.koin.error.BeanDefinitionException
import org.koin.error.NoBeanDefFoundException
import org.koin.error.NoScopeFoundException
import java.util.*
import kotlin.reflect.KClass

/**
 * Bean registry
 * gather definitions of beans & communicate with instance factory to handle instances
 *
 * @author - Arnaud GIULIANI
 */
class BeanRegistry {

    val definitions = HashMap<BeanDefinition<*>, Scope>()
    val scopes = arrayListOf<Scope>()
    val rootScope = Scope.root()

    init {
        scopes += rootScope
    }

    /**
     * Add/Replace an existing bean
     *
     * @param def : Bean definition
     */
    fun declare(def: BeanDefinition<*>, scope: Scope) {
        definitions += Pair(def, scope)
    }

    /**
     * Retrieve context scope for given bean definition
     */
    fun getScopeForDefinition(beanDefinition: BeanDefinition<*>) = definitions[beanDefinition]

    /**
     * Retrieve context scope for given class
     */
    private fun getScopeForClass(clazz: KClass<*>) = definitions[definitions.keys.first { it.clazz == clazz || it.bindTypes.contains(clazz) }]

    /**
     * Retrieve context scope for given name
     */
    fun getScope(name: String) = scopes.firstOrNull { it.name == name } ?: throw NoScopeFoundException("Context scope '$name' not found")

    /**
     * Find or create context scope
     */
    fun findOrCreateScope(scopeName: String?, parentScopeName: String? = null): Scope {
        return if (scopeName == null) rootScope
        else {
            scopes.firstOrNull { it.name == scopeName } ?: createScope(scopeName, parentScopeName)
        }
    }

    /**
     * Create context scope
     */
    fun createScope(scope: String, parentScope: String?): Scope {
        Koin.logger.log("[Scope] create [$scope] with parent [$parentScope]")
        val s = Scope(scope, parent = findOrCreateScope(parentScope))
        scopes += s
        return s
    }

    /**
     * Search bean by its name
     */
    fun searchByName(name: String) = searchDefinition({ it.name == name }, " name : $name") ?: throw NoBeanDefFoundException("No bean definition found for name $name")

    /**
     * Search for any bean definition
     */
    fun searchAll(clazz: kotlin.reflect.KClass<*>) = (searchByClass(clazz) ?: searchCompatible(clazz)) ?: throw NoBeanDefFoundException("No bean definition found for class $clazz")

    /**
     * Search for a bean definition
     */
    fun searchByClass(clazz: kotlin.reflect.KClass<*>) = searchDefinition({ it.clazz == clazz }, " class : $clazz")

    /**
     * Search definition with given filter function
     */
    private fun searchDefinition(filter: (BeanDefinition<*>) -> Boolean, errorMsg: String): BeanDefinition<*>? {
        val results = definitions.keys.filter(filter)
        return if (results.size <= 1) results.firstOrNull()
        else throw BeanDefinitionException("Bean definition resolution error : no bean or multiple definition for $errorMsg")
    }

    /**
     * Search for a compatible bean definition (subtype type of given clazz)
     */
    private fun searchCompatible(clazz: kotlin.reflect.KClass<*>): BeanDefinition<*>? = searchDefinition({ it.bindTypes.contains(clazz) }, "for compatible type : $clazz")

    /**
     * Get bean definitions from given scope context & child
     */
    fun getDefinitionsFromScope(name: String): List<BeanDefinition<*>> {
        val scopes = allScopesfrom(name).toSet()
        return definitions.keys.filter { def -> definitions[def] in scopes }
    }

    /**
     * Retrieve scope and child for given name
     */
    private fun allScopesfrom(name: String): List<Scope> {
        val scope = getScope(name)
        val firstChild = scopes.filter { it.parent == scope }
        return listOf(scope) + firstChild + firstChild.flatMap { allScopesfrom(it.name) }
    }

    /**
     * Is class/bean definition visible with given class list context
     */
    fun isVisible(clazz: KClass<*>, resolutionStack: List<KClass<*>>): Boolean {
        return if (resolutionStack.isEmpty()) true
        else {
            val pop = resolutionStack.last()
            if (resolutionStack.isNotEmpty()) {
                isVisibleScope(clazz, pop) && isVisible(clazz, resolutionStack - pop)
            } else {
                isVisibleScope(clazz, pop)
            }
        }
    }


    private fun isVisibleScope(clazz: KClass<*>, parentClass: KClass<*>): Boolean {
        val child = getScopeForClass(clazz) ?: error("$clazz has no scope")
        val parent = getScopeForClass(parentClass) ?: error("$parentClass has no Scope")
        return child.isVisible(parent)
    }

    /**
     * Clear resources
     */
    fun clear() {
        definitions.clear()
        scopes.clear()
    }
}