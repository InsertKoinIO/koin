package org.koin

import org.koin.Koin.Companion.logger
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceFactory
import org.koin.core.parameter.Parameters
import org.koin.core.property.PropertyRegistry
import org.koin.dsl.context.ParameterHolder
import org.koin.error.ContextVisibilityException
import org.koin.error.DependencyResolutionException
import org.koin.error.MissingPropertyException
import org.koin.error.NoBeanDefFoundException
import org.koin.standalone.StandAloneKoinContext
import kotlin.reflect.KClass

/**
 * Koin Application Context
 * Context from where you can get beans defined in modules
 *
 * @author Arnaud GIULIANI
 */
class KoinContext(
    val beanRegistry: BeanRegistry,
    val propertyResolver: PropertyRegistry,
    val instanceFactory: InstanceFactory
) : StandAloneKoinContext {

    private val resolutionStack = ResolutionStack()

    var contextCallback: ContextCallback? = null

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(name: String = "", noinline parameters: Parameters = { emptyMap() }): T =
        if (name.isEmpty()) resolveByClass(parameters) else resolveByName(name, parameters)

    /**
     * Resolve a dependency for its bean definition
     * @param name bean definition name
     */
    inline fun <reified T> resolveByName(name: String, noinline parameters: Parameters): T =
        resolveInstance(T::class, parameters) { beanRegistry.searchByName(name) }

    /**
     * Resolve a dependency for its bean definition
     * by its inferred type
     */
    inline fun <reified T> resolveByClass(noinline parameters: Parameters): T =
        resolveByClass(T::class, parameters)

    /**
     * Resolve a dependency for its bean definition
     * byt its type
     */
    inline fun <reified T> resolveByClass(clazz: KClass<*>, noinline parameters: Parameters): T =
        resolveInstance(clazz, parameters) { beanRegistry.searchAll(clazz) }

    /**
     * Resolve a dependency for its bean definition
     * @param clazz - Class
     * @param parameters - Parameters
     * @param definitionResolver - function to find bean definitions
     */
    fun <T> resolveInstance(
        clazz: KClass<*>,
        parameters: Parameters,
        definitionResolver: () -> List<BeanDefinition<*>>
    ): T = synchronized(this) {

        val clazzName = clazz.java.canonicalName

        var resultInstance: T? = null

        val beanDefinition: BeanDefinition<*> =
            getVisibleBeanDefinition(clazzName, definitionResolver, resolutionStack.last())

        val logIndent = resolutionStack.indent()
        resolutionStack.resolve(beanDefinition) {

            // Resolution log
            logger.log("${logIndent}Resolve class[$clazzName] with $beanDefinition")

            val (instance, created) = instanceFactory.retrieveInstance<T>(beanDefinition, ParameterHolder(parameters))

            // Log creation
            if (created) {
                logger.log("$logIndent(*) Created")
            }

            resultInstance = instance
        }
        return if (resultInstance != null) resultInstance!! else error("Could not create instance for $clazz")
    }

    /**
     * Retrieve bean definition
     * @param clazzName - class name
     * @param definitionResolver - function to find bean definition
     * @param lastInStack - to check visibility with last bean in stack
     */
    fun getVisibleBeanDefinition(
        clazzName: String,
        definitionResolver: () -> List<BeanDefinition<*>>,
        lastInStack: BeanDefinition<*>?
    ): BeanDefinition<*> {
        val candidates: List<BeanDefinition<*>> = (if (lastInStack != null) {
            val found = definitionResolver()
            val filteredByVisibility = found.filter { it.scope.isVisible(lastInStack.scope) }
            if (found.isNotEmpty() && filteredByVisibility.isEmpty()) throw ContextVisibilityException("Can't resolve '$clazzName' for definition $lastInStack.\n\tClass '$clazzName' is not visible from context scope ${lastInStack.scope}")
            filteredByVisibility
        } else definitionResolver()).distinct()

        if (candidates.size == 1) {
            return candidates.first()
        }

        if (candidates.isEmpty())
            throw NoBeanDefFoundException("No definition found to resolve type '$clazzName'. Check your module definition")

        throw DependencyResolutionException(
            "Multiple definitions found to resolve type '$clazzName' - Koin can't choose between :\n\t${candidates.joinToString(
                "\n\t"
            )}\n\tCheck your modules definition or use name attribute to resolve components."
        )

    }

    /**
     * Check all loaded definitions by resolving them one by one
     */
    fun dryRun(defaultParameters: Parameters) {
        logger.log("(DRY RUN)")
        beanRegistry.definitions.forEach { def ->
            Koin.logger.log("Testing $def ...")
            instanceFactory.retrieveInstance<Any>(def, ParameterHolder(defaultParameters))
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
     */
    fun setProperty(key: String, value: Any) = propertyResolver.add(key, value)

    /**
     * Delete properties from keys
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
