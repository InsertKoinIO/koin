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
     * Inject all system properties to context
     */
    fun bindSystemProperties(): Koin {
        val systemProps: Properties = System.getProperties()

        val nb = systemProps.keys
                .filter { it is String && systemProps[it] != null }
                .map { propertyResolver.setProperty(it as String, systemProps[it]!!) }
                .count()

        logger.log("(Koin) loaded $nb system properties")

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