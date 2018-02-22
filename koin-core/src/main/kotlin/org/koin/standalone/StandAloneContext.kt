package org.koin.standalone

import org.koin.Koin
import org.koin.KoinContext
import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceFactory
import org.koin.core.property.PropertyRegistry
import org.koin.dsl.module.Module
import org.koin.error.AlreadyStartedException

/**
 * Koin agnostic context support
 * @author - Arnaud GIULIANI
 */
object StandAloneContext {

    private var isStarted = false

    /**
     * Koin Context
     */
    lateinit var koinContext: StandAloneKoinContext

    /**
     * Koin starter
     */
    fun startKoin(list: List<Module>, useEnvironmentProperties: Boolean = false, properties: Map<String, Any> = HashMap()): Koin = synchronized(this) {
        if (isStarted && !Koin.allowKoinContextShare) {
            throw AlreadyStartedException()
        }

        val koin = if (!isStarted) {
            Koin.logger.log("[start] create context")
            val beanRegistry = BeanRegistry()
            val propertyResolver = PropertyRegistry()
            val instanceFactory = InstanceFactory()
            val newContext = KoinContext(beanRegistry, propertyResolver, instanceFactory)
            initProperties(Koin(newContext), useEnvironmentProperties, properties)
        } else {
            Koin.logger.log("[start] reuse context")
            val currentContext = koinContext as KoinContext
            initProperties(Koin(currentContext), useEnvironmentProperties, properties)
        }
        koinContext = koin.koinContext
        isStarted = true
        return koin.build(list)
    }

    private fun initProperties(k: Koin, useEnvironmentProperties: Boolean, properties: Map<String, Any>): Koin {
        var koin = k.bindKoinProperties().bindAdditionalProperties(properties)
        if (useEnvironmentProperties) {
            koin = koin.bindEnvironmentProperties()
        }
        return koin
    }

    /**
     * Close actual Koin context
     */
    fun closeKoin() = synchronized(this) {
        if (isStarted) {
            // Close all
            (koinContext as KoinContext).close()
            isStarted = false
        }
    }
}

/**
 * Stand alone Koin context
 */
interface StandAloneKoinContext