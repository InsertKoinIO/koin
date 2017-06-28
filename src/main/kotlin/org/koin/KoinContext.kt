package org.koin

import org.koin.bean.BeanRegistry
import org.koin.context.Scope
import org.koin.error.MissingPropertyException
import org.koin.instance.InstanceResolver
import org.koin.property.PropertyResolver
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Created by arnaud on 28/06/2017.
 */
class KoinContext(val beanRegistry: BeanRegistry, val propertyResolver: PropertyResolver, val instanceResolver: InstanceResolver) {

    val logger: Logger = Logger.getLogger(KoinContext::class.java.simpleName)

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(): T {
        return instanceResolver.resolveInstance(beanRegistry.searchAll(T::class))
    }

    /**
     * Retrieve a bean instance or null
     */
    inline fun <reified T> getOrNull(): T? {
        try {
            return get<T>()
        } catch(e: Exception) {
            logger.warning("No bean found due to error $e - return null")
            return null
        }
    }

    /**
     * provide bean definition
     * @param functional decleration
     */
    inline fun <reified T : Any> provide(noinline definition: () -> T) {
        logger.finest("declare singleton $definition")
        instanceResolver.deleteInstance(T::class, scope = Scope.root())
        beanRegistry.declare(definition, T::class, Scope.root())
    }

    /**
     * Remove a definitions and instances for given classes
     */
    fun remove(vararg classes: KClass<*>) {
        logger.info("Remove definition & isntance for $classes")
        beanRegistry.remove(*classes)
        instanceResolver.deleteInstance(*classes, scope = Scope.root())
    }

    /**
     * Delete instance for given classes
     */
    fun delete(vararg classes: KClass<*>) {
        logger.info("Remove instance for $classes ")
        instanceResolver.deleteInstance(*classes, scope = Scope.root())
    }

//    /**
//     * Inject bean into fields of target object
//     * Use introspection
//     * @param target
//     */
//    inline fun <reified T : Any> inject(target: T) {
////        logger.info("start inject ...")
////        val clazz = T::class
////        val fields = clazz.java.fields.filter { it.isAnnotationPresent(Inject::class.java) }.map { it.name }
////        val memberToInject = clazz.members.filter { it.name in fields }
////
////        logger.info("detected fields to inject : $fields")
////
////        memberToInject.forEach { resolveInjection<Any>(target, it as KMutableProperty<Any>) }
////
////        logger.info("all injected !")
//    }
//
//
//    /**
//     * Resolve property injection for given target instance
//     */
//    fun <T : Any> resolveInjection(target: Any, member: KMutableProperty<T>) {
////        val instance: Any = resolveInstance(member.returnType.classifier as KClass<*>)
////        member.setter.call(target, instance)
//    }
//
//    /**
//     * Retrieve a bean instance for given Clazz
//     * @param clazz
//     */
//    @Throws(NoBeanDefFoundException::class)
//    fun <T> resolveInstance(clazz: KClass<*>): T {
//        logger.info("resolve instance for $clazz")
//
//        var def = beanRegistry.searchDefinition(clazz)
//        if (def == null) {
//            def = beanRegistry.searchCompatibleDefinition(clazz)
//            logger.info("found compatible type for $clazz ? $def")
//        }
//
//        if (def != null) {
//            return instanceFactory.resolveInstance<T>(def, clazz)
//        } else {
//            throw NoBeanDefFoundException("Can't find bean definition for $clazz")
//        }
//    }
//
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
}