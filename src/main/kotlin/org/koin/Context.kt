package org.koin

import org.koin.bean.BeanType
import org.koin.error.BeanDefinitionException
import org.koin.error.MissingPropertyException
import org.koin.error.NoBeanDefFoundException
import org.koin.module.Module
import java.util.logging.Logger
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance

/**
 * Koin Context
 * Define dependencies & properties for actual context
 * @author - Arnaud GIULIANI
 */
class Context(val beanRegistry: BeanRegistry = BeanRegistry(), val instanceFactory: InstanceFactory = InstanceFactory(), val propertyResolver: PropertyResolver = PropertyResolver()) {

    val logger: Logger = Logger.getLogger(Context::class.java.simpleName)

    /**
     * Inject bean into fields of target object
     * Use introspection
     * @param target
     */
    inline fun <reified T : Any> inject(target: T) {
        logger.info("start inject ...")
        val clazz = T::class
        val fields = clazz.java.fields.filter { it.isAnnotationPresent(Inject::class.java) }.map { it.name }
        val memberToInject = clazz.members.filter { it.name in fields }

        logger.info("detected fields to inject : $fields")

        memberToInject.forEach { resolveInjection<Any>(target, it as KMutableProperty<Any>) }

        logger.info("all injected !")
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
     * Resolve property injection for given target instance
     */
    fun <T : Any> resolveInjection(target: Any, member: KMutableProperty<T>) {
        val instance: Any = resolveInstance(member.returnType.classifier as KClass<*>)
        member.setter.call(target, instance)
    }


    /**
     * Retrieve a property
     */
    @Throws(MissingPropertyException::class)
    inline fun <reified T> getProperty(key: String): T = getPropertyOrNull(key) ?: throw MissingPropertyException("Could not bind property $key")

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
        return resolveInstance(T::class)
    }

    /**
     * Retrieve a bean instance or null
     */
    inline fun <reified T> getOrNull(): T? {
        val clazz: KClass<*> = T::class
        try {
            return resolveInstance(clazz)
        } catch(e: Exception) {
            logger.warning("couldn't get bean for $clazz - due to error : $e")
            return null
        }
    }

    /**
     * Retrieve a bean instance for given Clazz
     * @param clazz
     */
    @Throws(NoBeanDefFoundException::class)
    fun <T> resolveInstance(clazz: KClass<*>): T {
        logger.info("resolve instance for $clazz")

        var def = beanRegistry.searchDefinition(clazz)
        if (def == null) {
            def = beanRegistry.searchCompatibleDefinition(clazz)
            logger.info("found compatible type for $clazz ? $def")
        }

        if (def != null) {
            return instanceFactory.resolveInstance<T>(def, clazz)
        } else {
            throw NoBeanDefFoundException("Can't find bean definition for $clazz")
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
     * provide bean definition
     * @param functional decleration
     */
    inline fun <reified T : Any> provide(noinline definition: () -> T) {
        logger.finest("declare singleton $definition")
        instanceFactory.remove(T::class)
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
            throw BeanDefinitionException("class $clazz has no constructor")
        } else {
            val ctor = clazz.constructors.first()
            val types = ctor.parameters
            if (types.isEmpty()) {
                beanRegistry.declare({ clazz.createInstance() }, clazz, type)
            } else {
                beanRegistry.declare({
                    val instances: Map<KParameter, Any> = types.map { it to resolveInstance<Any>(it.type.classifier as KClass<*>) }.toMap()
                    ctor.callBy(instances)
                }, clazz, type)
            }
        }
    }

    /**
     * Remove a definitions and instances for given classes
     */
    fun remove(vararg classes: KClass<*>) {
        logger.info("Remove definition & isntance for $classes")
        beanRegistry.remove(*classes)
        instanceFactory.remove(*classes)
    }

    /**
     * Delete instance for given classes
     */
    fun delete(vararg classes: KClass<*>) {
        logger.info("Remove instance for $classes ")
        instanceFactory.remove(*classes)
    }
}