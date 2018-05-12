package org.koin.standalone

import org.koin.core.ContextCallback
import org.koin.core.Koin
import org.koin.core.KoinContext
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
     * Load Koin modules - whether Koin is already started or not
     * allow late module definition load (e.g: libraries ...)
     *
     * @param modules : List of Module
     */
    fun loadKoinModules(modules: List<Module>): Koin = synchronized(this) {
        createContextIfNeeded()
        return KoinContext().build(modules)
    }

    /**
     * Get koin context
     */
    private fun KoinContext(): Koin = Koin(koinContext as KoinContext)

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
     * @param useKoinPropertiesFile - koin.properties file
     * @param extraProperties - additional properties
     */
    fun loadProperties(
        useEnvironmentProperties: Boolean = false,
        useKoinPropertiesFile: Boolean = true,
        extraProperties: Map<String, Any> = HashMap()
    ): Koin = synchronized(this) {
        createContextIfNeeded()

        val koin = KoinContext()

        if (useKoinPropertiesFile) {
            Koin.logger.log("[properties] load koin.properties")
            koin.bindKoinProperties()
        }

        if (extraProperties.isNotEmpty()) {
            Koin.logger.log("[properties] load extras properties : ${extraProperties.size}")
            koin.bindAdditionalProperties(extraProperties)
        }

        if (useEnvironmentProperties) {
            Koin.logger.log("[properties] load environment properties")
            koin.bindEnvironmentProperties()
        }
        return koin
    }

    /**
     * Koin starter function to load modules and extraProperties
     * Throw AlreadyStartedException if already started
     * @param list : Modules
     * @param useEnvironmentProperties - use environment extraProperties
     * @param useKoinPropertiesFile - use /koin.extraProperties file
     * @param extraProperties - extra extraProperties
     */
    fun startKoin(
        list: List<Module>,
        useEnvironmentProperties: Boolean = false,
        useKoinPropertiesFile: Boolean = true,
        extraProperties: Map<String, Any> = HashMap()
    ): Koin {
        if (isStarted) {
            throw AlreadyStartedException("Koin is already started. Run startKoin only once or use loadKoinModules")
        }
        createContextIfNeeded()
        loadKoinModules(list)
        loadProperties(useEnvironmentProperties, useKoinPropertiesFile, extraProperties)
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