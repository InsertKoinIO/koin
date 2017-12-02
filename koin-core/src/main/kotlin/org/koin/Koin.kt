package org.koin

import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceFactory
import org.koin.core.property.PropertyRegistry
import org.koin.dsl.context.Context
import org.koin.dsl.module.Module
import org.koin.log.EmptyLogger
import org.koin.log.Logger
import java.io.File
import java.io.FileInputStream
import java.util.*

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
    fun bindAdditionalProperties(props: Map<String, Any>): Koin {
        if (props.isNotEmpty()) {
            propertyResolver.addAll(props)
        }
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
            val nb = propertyResolver.import(koinProperties)
            logger.log("[init] loaded $nb properties from '$koinPropFilename' file")
        }
        return this
    }

    /**
     * Inject all system properties to context
     */
    fun bindSystemProperties(): Koin {
        val nb = propertyResolver.import(System.getProperties())
        logger.log("[init] loaded $nb properties from system properties")
        return this
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

        logger.log("[init] loaded ${beanRegistry.definitions.size} definitions")
        return koinContext
    }

    /**
     * Register context definitions & subContexts
     */
    private fun registerDefinitions(context: Context, parentContext: Context? = null) {
        // Create or reuse getScopeForDefinition context
        val scope = beanRegistry.findOrCreateScope(context.name, parentContext?.name)

        // Add definitions
        context.definitions.forEach { definition ->
            logger.log("[init] declare : $definition")
            beanRegistry.declare(definition, scope)
        }

        // Check sub contexts
        context.subContexts.forEach { subContext -> registerDefinitions(subContext, context) }
    }

    companion object {
        /**
         * Koin Logger
         */
        var logger: Logger = EmptyLogger()
    }
}