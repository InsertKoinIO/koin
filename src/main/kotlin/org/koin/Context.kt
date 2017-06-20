package org.koin

import org.koin.bean.BeanType
import org.koin.error.BeanDefinitionException
import org.koin.module.Module
import java.util.logging.Logger
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties

/**
 * Koin Context
 * Define dependencies & properties for actual context
 * @author - Arnaud GIULIANI
 */
class Context(val beanRegistry: BeanRegistry = BeanRegistry(), val propertyResolver: PropertyResolver = PropertyResolver()) {

    val logger: Logger = Logger.getLogger(Context::class.java.simpleName)

    /**
     * Inject bean into fields of target object
     * Use introspection
     * @param target
     */
    inline fun <reified T : Any> inject(target: T) {
        val clazz = T::class
        val fields = target.javaClass.declaredFields
        val fieldsToInject = fields.filter { it.annotations.filter { it is Inject }.isNotEmpty() }.map { it.name }

        clazz.declaredMemberProperties.filter { it.name in fieldsToInject }.forEach { p ->
            beanRegistry.resolveInjection(target, p)
        }
    }

    /**
     * import modules definitions
     * @param modules
     */
    fun import(vararg modules: KClass<out Module>) = modules.forEach {
        logger.finest("importing modules : $modules")

        val module = it.createInstance()
        module.context = this
        module.onLoad()
    }

    /**
     * Retrieve a property
     */
    inline fun <reified T> getProperty(key: String): T = getPropertyOrNull(key)!!

    /**
     * Retrieve safely a property
     */
    inline fun <reified T> getPropertyOrNull(key: String): T? = propertyResolver.getProperty(key)

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any) = propertyResolver.setProperty(key, value)

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(): T {
        val clazz: KClass<*> = T::class
        return beanRegistry.resolveInstance(clazz)
    }

    /**
     * Retrieve a bean instance or null
     */
    inline fun <reified T> getOrNull(): T? {
        val clazz: KClass<*> = T::class
        try {
            return beanRegistry.resolveInstance(clazz)
        } catch(e: Exception) {
            return null
        }
    }

    /**
     * provide bean definition - As factory
     * @param functional decleration
     */
    inline fun <reified T : Any> factory(noinline definition: () -> T) {
        logger.finest("declare factory $definition")
        beanRegistry.declare(definition, T::class, BeanType.FACTORY)
    }

    /**
     * provide bean definition - As factory
     * @param clazz
     */
    fun factory(clazz: KClass<*>) {
        logger.finest("declare factory class $clazz")
        provide(clazz, BeanType.FACTORY)
    }


    /**
     * Provide bean definition for single use instance
     */
    inline fun <reified T : Any> stack(noinline definition: () -> T) {
        logger.finest("declare stack $definition")

        beanRegistry.declare(definition, T::class, BeanType.STACK)
    }

    /**
     * provide bean definition
     * @param functional decleration
     */
    inline fun <reified T : Any> provide(noinline definition: () -> T) {
        logger.finest("declare singleton $definition")
        beanRegistry.declare(definition, T::class)
    }

    /**
     * provide bean definition for class
     * @param clazz
     */
    @Throws(BeanDefinitionException::class)
    fun provide(clazz: KClass<*>, type: BeanType = BeanType.SINGLETON) {
        logger.finest("declare class : $clazz - type : $type")

        if (clazz.constructors.isEmpty()) {
            throw IllegalStateException("class $clazz has no constructor")
        } else {
            val ctor = clazz.constructors.first()
            val types = ctor.parameters
            if (types.isEmpty()) {
                beanRegistry.declare({ clazz.createInstance() }, clazz, type)
            } else {
                beanRegistry.declare({
                    val instances: Map<KParameter, Any> = types.map { it to beanRegistry.resolveInstance<Any>(it.type.classifier as KClass<*>) }.toMap()
                    ctor.callBy(instances)
                }, clazz, type)
            }
        }
    }

    /**
     * Remove a definition and instance for gvien class
     */
    fun remove(kClass: KClass<*>) {
        logger.info("Remove definition & isntance for $kClass")
        beanRegistry.remove(kClass)
    }

    /**
     * Remove a definitions and instances for given classes
     */
    fun remove(vararg classes: KClass<*>) {
        classes.map { remove(it) }
    }

    /**
     * Delete instance for given class
     */
    fun delete(kClass: KClass<*>) {
        logger.info("Remove instance for $kClass ")
        beanRegistry.instanceFactory.instances.remove(kClass)
    }

    /**
     * Delete instance for given classes
     */
    fun delete(vararg classes: KClass<*>) {
        classes.map { delete(it) }
    }
}