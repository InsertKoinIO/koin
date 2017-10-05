package org.koin

import org.koin.bean.BeanRegistry
import org.koin.dsl.context.Scope
import org.koin.dsl.module.Module
import org.koin.instance.InstanceResolver
import org.koin.property.PropertyResolver

/**
 * Koin Context Builder
 * @author - Arnaud GIULIANI
 */
class Koin {

//    val logger: Logger = Logger.getLogger(Koin::class.java.simpleName)

    val beanRegistry = BeanRegistry()
    val propertyResolver = PropertyResolver()
    val instanceResolver = InstanceResolver()

    init {
//        logger.info("(-) Koin Started ! (-)")
        instanceResolver.createContext(Scope.root())
    }

    /**
     * Inject properties to context
     */
    fun properties(props: Map<String, Any>): Koin {
        propertyResolver.addAll(props)
        return this
    }

    /**
     * load given list of module instances into current koin context
     */
    fun <T : Module> build(modules: List<T>): KoinContext {
        val koinContext = KoinContext(beanRegistry, propertyResolver, instanceResolver)
        modules.forEach {
            it.koinContext = koinContext
            val ctx = it.context()
            val scope = ctx.scope
            if (!instanceResolver.scopeExists(scope)) {
                instanceResolver.createContext(scope)
            }
            ctx.provided.forEach { beanRegistry.declare(it) }
        }
        return koinContext
    }

    /**
     * load given module instances into current koin context
     */
    fun <T : Module> build(vararg modules: T): KoinContext = build(modules.asList())

    /**
     * load directly Koin context with no modules
     */
    fun build(): KoinContext = KoinContext(beanRegistry, propertyResolver, instanceResolver)
}