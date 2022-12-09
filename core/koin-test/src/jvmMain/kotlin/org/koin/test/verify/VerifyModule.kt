package org.koin.test.verify

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.Module
import org.koin.core.module.flatten
import org.koin.core.time.Timer
import org.koin.ext.getFullName
import org.koin.mp.KoinPlatformTimeTools
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility

/**
 * Make a static verification about all declared classes constructors, to ensure they are all bound to an existing definition
 * Throws MissingDefinitionException if any definition is missing
 *
 * @param extraTypes - allow to declare extra type, to be bound above the existing definitions
 */
@OptIn(KoinInternalApi::class)
fun Module.verify(extraTypes : List<KClass<*>> = listOf()){
    val timer = Timer.start()
    val allModules = flatten(includedModules.toList()) + this
    val factories = allModules.flatMap { it.mappings.values.toList() }
    val extraKeys = extraTypes.map { it.getFullName() }
    val keys = allModules.flatMap { it.mappings.keys.toList() } + extraKeys
    println("Verifying Module '$this' ...")
    println("- index: $keys")
    val verified = arrayListOf<InstanceFactory<*>>()
    factories.forEach { factory ->
        if (factory !in verified){
            verifyFactory(factory,keys)
            verified.add(factory)
        }
    }
    timer.stop()
    val time = if (timer.getTimeInMillis() < 1000) "${timer.getTimeInMillis()} ms" else "${timer.getTimeInSeconds()} sec"
    println("[SUCCESS] module '$this' has been verified in $time.")
}

private fun verifyFactory(factory: InstanceFactory<*>, keys: List<String>) {
    val beanDefinition = factory.beanDefinition
    println("|- $beanDefinition")
    val allTypes = listOf(beanDefinition.primaryType) + beanDefinition.secondaryTypes
    println("| bound types : $allTypes")
    allTypes.forEach { boundType ->
        val publicConstructors = boundType.constructors.filter { it.visibility == KVisibility.PUBLIC }
        publicConstructors.forEach { ctor ->
            verifyConstructor(ctor, boundType, keys,beanDefinition)
        }
    }
}

private fun verifyConstructor(
    ctor: KFunction<*>,
    boundType: KClass<*>,
    keys: List<String>,
    beanDefinition: BeanDefinition<*>
) {
    val types = ctor.parameters
    println("| constructor: $boundType $types")
    types.forEach { type ->
        val kc = type.type.classifier as KClass<*>
        val fn = kc.getFullName()
        val indexExists = keys.any { k -> k.contains(fn) }
        if (!indexExists) {
            System.err.println("* ----- > Missing type '${kc.qualifiedName}' for class '${boundType.qualifiedName}' in definition '$beanDefinition'\nFix your Koin configuration or add extraTypes parameter: verify(extraTypes = listOf(${kc.qualifiedName}::class))")
            throw MissingDefinitionException("Missing type '${kc.qualifiedName}' for class '${boundType.qualifiedName}' in definition '$beanDefinition'")
        }
        else {
            println("|-> '$kc'")
        }
    }
}

/**
 * Verify a list of Modules
 *
 * @see Module.verify
 */
fun List<Module>.verifyAll(extraTypes : List<KClass<*>> = listOf()){
    forEach { module -> module.verify(extraTypes) }
}