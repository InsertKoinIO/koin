package org.koin.test.ext.koin

import org.koin.core.Koin
import org.koin.core.KoinContext
import org.koin.core.bean.BeanRegistry
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.test.error.BrokenDefinitionException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * Check all loaded definitions by resolving them one by one
 */
fun KoinContext.dryRun(defaultParameters: ParameterDefinition) {
    Koin.logger.log("(DRY RUN)")
    beanRegistry.definitions.forEach { def ->
        Koin.logger.log("Testing instance $def ...")
        instanceFactory.retrieveInstance<Any>(def, defaultParameters)
    }
}

/**
 * Check all definition references
 * Ensure that your definitions are consistent (no missing dependency)
 */
fun KoinContext.check() {
    Koin.logger.log("(CHECK)")
    val definitions = beanRegistry.definitions
    definitions.forEach { def ->
        checkDefinition(def, beanRegistry)
    }
}

/**
 * Check that definition's first constructor has all its dependencies registered
 */
fun checkDefinition(def: BeanDefinition<*>, beanRegistry: BeanRegistry) {
    val finalType = def.clazz
    Koin.logger.log("Checking definition: $def ...")
    val ctor = finalType.constructors.firstOrNull()
    if (ctor != null) {
        checkConstructor(def, ctor, beanRegistry)
    }
}

/**
 * Check that Constructor parameters are registered in Koin container
 */
fun checkConstructor(def: BeanDefinition<*>, ctor: KFunction<Any>, beanRegistry: BeanRegistry) {
    val params = ctor.parameters
    if (params.isNotEmpty()) {
        params.forEach { param ->
            val clazz = param.type.classifier as KClass<*>
            Koin.logger.log("- checking dependency type '$clazz' ...")
            if (beanRegistry.searchAll(clazz).isEmpty()) {
                Koin.logger.err("- definition $def is broken!")
                throw BrokenDefinitionException("Could not retrieve dependency of type '$clazz' for definition $def")
            }
            Koin.logger.log("- definition is ok!")
        }
    } else {
        Koin.logger.log("- no needed dependency")
    }
}

/**
 * Return all definitions of Koin
 */
fun KoinContext.beanDefinitions() = beanRegistry.definitions

/**
 * return beanDefinition for given class
 * @param clazz - bean class
 */
fun KoinContext.beanDefinition(clazz: KClass<*>): BeanDefinition<*>? =
    beanDefinitions().firstOrNull() { it.clazz == clazz }

/**
 * Return all contexts of Koin
 */
fun KoinContext.allPaths() = pathRegistry.paths

/**
 * Return all instances of Koin
 */
fun KoinContext.allInstances() = instanceFactory.instances.toList()

/**
 * Return all properties of Koin
 */
fun KoinContext.allProperties() = propertyResolver.properties

/**
 * return path
 * @param path
 */
fun KoinContext.getPath(path: String) = allPaths().first { it.name == path }
