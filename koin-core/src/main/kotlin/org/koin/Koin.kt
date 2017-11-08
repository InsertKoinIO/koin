package org.koin

import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceFactory
import org.koin.core.property.PropertyRegistry
import org.koin.core.scope.Scope
import org.koin.dsl.context.Context
import org.koin.dsl.module.Module
import org.koin.log.EmptyLogger
import org.koin.log.Logger
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.reflect.KClass

/**
 * Koin Context Builder
 * @author - Arnaud GIULIANI
 */
class Koin {
    val beanRegistry = BeanRegistry()
    val propertyResolver = PropertyRegistry()
    val instanceFactory = InstanceFactory(beanRegistry)

    /**
     * Inject properties to context
     */
    fun properties(props: Map<String, Any>): Koin {
        propertyResolver.addAll(props)
        return this
    }

    /**
     * Inject all properties from koin properties file to context
     */
    fun bindKoinProperties(koinPropFilename: String = "koin.properties"): Koin {
        val classLoader: ClassLoader = Koin::class.java.classLoader

        val path: String? = classLoader.getResource(koinPropFilename)?.path

        if (path != null && File(path).exists()) {

            val koinProperties = Properties()
            FileInputStream(path).use { koinProperties.load(it) }

            val nb = bindProperties(koinProperties)
            logger.log("(Koin) loaded $nb properties from $koinPropFilename file")
        }
        return this
    }

    /**
     * Inject all system properties to context
     */
    fun bindSystemProperties(): Koin {
        val nb = bindProperties(System.getProperties())
        logger.log("(Koin) loaded $nb system properties")
        return this
    }

    /**
     * Inject all properties to context
     */
    private fun bindProperties(properties: Properties): Int {
        return properties.keys
                .filter { it is String && properties[it] != null }
                .map { propertyResolver.setProperty(it as String, properties[it]!!) }
                .count()
    }

    /**
     * load given list of module instances into current koin context
     */
    fun <T : Module> build(modules: List<T>): KoinContext {
        val koinContext = KoinContext(beanRegistry, propertyResolver, instanceFactory)
        modules.forEach { module ->
            module.koinContext = koinContext
            val context = module.context()
            registerDefinitions(context)
        }

        logger.log("(Koin) loaded ${beanRegistry.definitions.size} definitions")

        return koinContext
    }

    /**
     * Provide a bean definition before building any module
     */
    inline fun <reified T : Any> provide(contextName: String = Scope.ROOT, additionalBinding: KClass<*>? = null, noinline definition: () -> T): Koin {
        val clazz = T::class
        val beanDefinition = BeanDefinition(clazz = clazz, definition = definition)
        if (additionalBinding != null) {
            beanDefinition.bind(additionalBinding)
        }
        beanRegistry.declare(beanDefinition, beanRegistry.getScope(contextName))
        return this
    }

    /**
     * Register context definitions & subContexts
     */
    private fun registerDefinitions(context: Context, parentContext: Context? = null) {
        // Create or reuse getScopeForDefinition context
        val scope = beanRegistry.findOrCreateScope(context.name, parentContext?.name)

        // Add definitions
        context.definitions.forEach { definition ->
            logger.log("(Koin) define : $definition")
            beanRegistry.declare(definition, scope)
        }

        // Check sub contexts
        context.subContexts.forEach { subContext -> registerDefinitions(subContext, context) }
    }

    /**
     * load given module instances into current koin context
     */
    fun <T : Module> build(vararg modules: T): KoinContext = build(modules.asList())

    companion object {
        /**
         * Koin Logger
         */
        var logger: Logger = EmptyLogger()
    }
}