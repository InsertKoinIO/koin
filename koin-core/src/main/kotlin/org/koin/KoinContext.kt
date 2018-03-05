package org.koin

import org.koin.Koin.Companion.logger
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceFactory
import org.koin.core.parameter.ParameterMap
import org.koin.core.property.PropertyRegistry
import org.koin.dsl.context.Parameters
import org.koin.error.ContextVisibilityException
import org.koin.error.DependencyResolutionException
import org.koin.error.MissingPropertyException
import org.koin.error.NoBeanDefFoundException
import org.koin.standalone.StandAloneKoinContext
import java.util.*
import kotlin.reflect.KClass

/**
 * Koin Application Context
 * Context from where you can get beans defines in modules
 *
 * @author Arnaud GIULIANI
 */
class KoinContext(
    val beanRegistry: BeanRegistry,
    val propertyResolver: PropertyRegistry,
    val instanceFactory: InstanceFactory
) : StandAloneKoinContext {

    /**
     * call stack - bean definition resolution
     */
    val resolutionStack = Stack<StackItem>()

    var contextCallback: ContextCallback? = null

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(name: String = "", noinline parameters: ParameterMap = { emptyMap() }): T =
        if (name.isEmpty()) resolveByClass(parameters) else resolveByName(name, parameters)

    /**
     * Resolve a dependency for its bean definition
     * @param name bean definition name
     */
    inline fun <reified T> resolveByName(name: String, noinline parameters: ParameterMap): T =
        resolveInstance(T::class, parameters) { beanRegistry.searchByName(name) }

    /**
     * Resolve a dependency for its bean definition
     * byt Its inferred type
     */
    inline fun <reified T> resolveByClass(noinline parameters: ParameterMap): T =
        resolveByClass(T::class, parameters)

    /**
     * Resolve a dependency for its bean definition
     * byt its type
     */
    inline fun <reified T> resolveByClass(clazz: KClass<*>, noinline parameters: ParameterMap): T =
        resolveInstance(clazz, parameters) { beanRegistry.searchAll(clazz) }

    /**
     * Resolve a dependency for its bean definition
     */
    inline fun <T> resolveInstance(
        clazz: KClass<*>,
        noinline paramsValue: ParameterMap,
        definitionResolver: () -> List<BeanDefinition<*>>
    ): T = synchronized(this) {
        val clazzName = clazz.java.canonicalName
        if (resolutionStack.any { it.isCompatibleWith(clazz) }) {
            throw DependencyResolutionException(
                "Cyclic call while resolving $clazzName. Definition is already in resolution in current call:\n\t${resolutionStack.joinToString(
                    "\n\t"
                )}"
            )
        }

        val lastInStack: BeanDefinition<*>? = if (resolutionStack.size > 0) resolutionStack.peek() else null

        val candidates: List<BeanDefinition<*>> = (if (lastInStack != null) {
            val found = definitionResolver()
            val filtered = found.filter { it.scope.isVisible(lastInStack.scope) }
            if (found.isNotEmpty() && filtered.isEmpty()) throw ContextVisibilityException("Can't resolve '$clazzName' for definition $lastInStack.\n\tClass '$clazzName' is not visible from context scope ${lastInStack.scope}")
            filtered
        } else definitionResolver()).distinct()

        val beanDefinition: BeanDefinition<*> = if (candidates.size == 1) {
            candidates.first()
        } else {
            when {
                candidates.isEmpty() -> throw NoBeanDefFoundException("No definition found to resolve type '$clazzName'. Check your module definition")
                else -> throw DependencyResolutionException(
                    "Multiple definitions found to resolve type '$clazzName' - Koin can't choose between :\n\t${candidates.joinToString(
                        "\n\t"
                    )}\n\tCheck your modules definition or use name attribute to resolve components."
                )
            }
        }

        val indent = resolutionStack.joinToString(separator = "") { "\t" }
        logger.log("${indent}Resolve class[$clazzName] with $beanDefinition")

        val parameters = Parameters(paramsValue)
        resolutionStack.add(beanDefinition)

        val (instance, created) = instanceFactory.retrieveInstance<T>(beanDefinition, parameters)
        if (created) {
            logger.log("$indent(*) Created")
        }

        val head: BeanDefinition<*> = resolutionStack.pop()

        if (!head.isCompatibleWith(clazz)) {
            resolutionStack.clear()
            throw IllegalStateException("Stack resolution error : was $head but should be $clazzName")
        }
        return instance
    }

    /**
     * Check the all the loaded definitions - Try to resolve each definition
     */
    fun dryRun(defaultParameters: ParameterMap) {
        logger.log("(DRY RUN)")
        beanRegistry.definitions.forEach { def ->
            Koin.logger.log("Testing $def ...")
            instanceFactory.retrieveInstance<Any>(def, Parameters(defaultParameters))
        }
    }

    /**
     * Drop all instances for given context
     * @param name
     */
    fun releaseContext(name: String) {
        logger.log("Release context : $name")

        val definitions: List<BeanDefinition<*>> = beanRegistry.getDefinitionsFromScope(name)
        instanceFactory.dropAllInstances(definitions)

        contextCallback?.onContextReleased(name)
    }

    /**
     * Retrieve a property by its key
     * can throw MissingPropertyException if the property is not found
     * @param key
     * @throws MissingPropertyException if key is not found
     */
    inline fun <reified T> getProperty(key: String): T = propertyResolver.getProperty(key)

    /**
     * Retrieve a property by its key or return provided default value
     * @param key - property key
     * @param defaultValue - default value if property is not found
     */
    inline fun <reified T> getProperty(key: String, defaultValue: T): T =
        propertyResolver.getProperty(key, defaultValue)

    /**
     * Set a property
     * @param key
     * @param value
     */
    fun setProperty(key: String, value: Any) = propertyResolver.add(key, value)

    /**
     * Delete properties from keys
     * @param keys
     */
    fun releaseProperties(vararg keys: String) {
        propertyResolver.deleteAll(keys)
    }

    /**
     * Close res
     */
    fun close() {
        logger.log("[Close] Closing Koin context")
        resolutionStack.clear()
        instanceFactory.clear()
        beanRegistry.clear()
        propertyResolver.clear()
    }
}

/**
 * Context callback
 */
interface ContextCallback {

    /**
     * Notify on context release
     * @param contextName - context name
     */
    fun onContextReleased(contextName: String)
}

typealias StackItem = BeanDefinition<*>
