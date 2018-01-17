package org.koin

import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceFactory
import org.koin.core.property.PropertyRegistry
import org.koin.dsl.context.Context
import org.koin.dsl.module.Module
import org.koin.log.Logger
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext
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
    fun bindKoinProperties(koinFile: String = "/koin.properties"): Koin {
        val content = Koin::class.java.getResource(koinFile)?.readText()
        content?.let {
            val koinProperties = Properties()
            koinProperties.load(content.byteInputStream())
            val nb = propertyResolver.import(koinProperties)
            logger.log("[init] loaded $nb properties from '$koinFile' file")
        }
        return this
    }

    /**
     * Inject all system properties to context
     */
    fun bindEnvironmentProperties(): Koin {
        val n1 = propertyResolver.import(System.getProperties())
        logger.log("[init] loaded $n1 properties from properties")
        val n2 = propertyResolver.import(System.getenv().toProperties())
        logger.log("[init] loaded $n2 properties from env properties")
        return this
    }

    /**
     * load given list of module instances into current StandAlone koin context
     */
    fun build(modules: List<Module>): Koin {
        StandAloneContext.koinContext = KoinContext(beanRegistry, propertyResolver, instanceFactory)

        modules.forEach { module ->
            registerDefinitions(module())
        }

        logger.log("[init] loaded ${beanRegistry.definitions.size} definitions")

        if (Koin.useContextIsolation) {
            logger.log("[init] context isolation activated")
        }
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
        var logger: Logger = PrintLogger()

        /**
         * Context isolation/visibility check
         */
        var useContextIsolation = false
    }
}