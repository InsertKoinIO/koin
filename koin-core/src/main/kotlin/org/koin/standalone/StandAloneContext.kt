package org.koin.standalone

import org.koin.ContextCallback
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
     * Load Koin modules - whether Koin is already started or not
     * allow late module definition load (e.g: libraries ...)
     *
     * @param modules : List of Module
     */
    fun loadKoinModules(vararg modules: Module): Koin = synchronized(this) {
        createContextIfNeeded()
        return KoinContext().build(modules.toList())
    }

    /**
     * Get koin context
     */
    private fun KoinContext() = Koin(koinContext as KoinContext)

    /**
     * Create Koin context if needed :)
     */
    private fun createContextIfNeeded() = synchronized(this) {
        if (!isStarted) {
            Koin.logger.log("[context] create")
            val beanRegistry = BeanRegistry()
            val propertyResolver = PropertyRegistry()
            val instanceFactory = InstanceFactory()
            koinContext = KoinContext(beanRegistry, propertyResolver, instanceFactory)
            isStarted = true
        }
    }

    /**
     * Register Context callbacks
     * @see ContextCallback - Context CallBack
     */
    fun registerContextCallBack(contextCallback: ContextCallback) {
        Koin.logger.log("[context] callback registering with $contextCallback")
        KoinContext().koinContext.contextCallback = contextCallback
    }

    /**
     * Load Koin properties - whether Koin is already started or not
     * Will look at koin.properties file
     *
     * @param useEnvironmentProperties - environment properties
     * @param additionalProperties - additional properties
     */
    fun loadProperties(useEnvironmentProperties: Boolean = false, additionalProperties: Map<String, Any> = HashMap()): Koin = synchronized(this) {
        createContextIfNeeded()

        val koin = KoinContext()

        Koin.logger.log("[properties] load koin.properties")
        koin.bindKoinProperties()

        if (additionalProperties.isNotEmpty()) {
            Koin.logger.log("[properties] load extras properties : ${additionalProperties.size}")
            koin.bindAdditionalProperties(additionalProperties)
        }

        if (useEnvironmentProperties) {
            Koin.logger.log("[properties] load environment properties")
            koin.bindEnvironmentProperties()
        }
        return koin
    }

    /**
     * Koin starter function to load modules and properties
     * Throw AlreadyStartedException if already started
     */
    fun startKoin(modules: List<Module>, useEnvironmentProperties: Boolean = false, properties: Map<String, Any> = HashMap()): Koin {
        if (isStarted) {
            throw AlreadyStartedException("Koin is already started. Run startKoin only once or use loadKoinModules")
        }
        createContextIfNeeded()
        loadKoinModules(*modules.toTypedArray())
        loadProperties(useEnvironmentProperties, properties)
        return KoinContext()
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
