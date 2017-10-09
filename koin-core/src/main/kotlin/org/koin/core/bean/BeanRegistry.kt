package org.koin.core.bean

import org.koin.core.scope.Scope
import org.koin.error.BeanDefinitionException
import org.koin.error.NoBeanDefFoundException
import kotlin.reflect.KClass

/**
 * Bean registry
 * gather definitions of beans & communicate with instance factory to handle instances
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
     * TODO
     */
    fun getScope(beanDefinition: BeanDefinition<*>) = definitions[beanDefinition]

    /**
     * TODO
     */
    fun getScope(name: String) = scopes.first { it.name == name }

    /**
     * TODO
     */
    fun findOrCreateScope(scopeName: String?, parentScopeName: String? = null): Scope {
        return if (scopeName == null) rootScope
        else {
            scopes.firstOrNull { it.name == scopeName } ?: createScope(scopeName, parentScopeName)
        }
    }

    /**
     * TODO
     */
    fun createScope(scope: String, parentScope: String?): Scope {
        val s = Scope(scope, parent = findOrCreateScope(parentScope))
        scopes += s
        return s
    }

    /**
     * Add/Replace an existing bean
     *
     * @param function : Declaration function bean
     * @param clazz : Bean Type
     * @param getScope : Bean getScope
     */
    inline fun <reified T : Any> declare(noinline function: () -> T, clazz: kotlin.reflect.KClass<*> = T::class, getScope: Scope) {
        val def = BeanDefinition(clazz = clazz, definition = function)

        val found = searchByClass(clazz)
        if (found != null) {
            remove(clazz)
        }
        definitions += Pair(def, getScope)
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
     * move any definition for given class
     * @param classes Class
     */
    fun remove(vararg classes: KClass<*>) {
        classes.map { searchByClass(it) }.forEach { definitions.remove(it) }
    }

}