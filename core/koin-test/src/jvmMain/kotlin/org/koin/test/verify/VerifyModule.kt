package org.koin.test.verify

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.Module
import org.koin.core.module.flatten
import org.koin.core.time.Timer
import org.koin.ext.getFullName
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility

/**
 * Make a static verification about all declared classes constructors, to ensure they are all bound to an existing definition
 * Throws MissingDefinitionException if any definition is missing
 *
 * @param extraTypes - allow to declare extra type, to be bound above the existing definitions
 * @throws MissingKoinDefinitionException
 */
@KoinExperimentalAPI
fun Module.verify(extraTypes: List<KClass<*>> = listOf()) = Verify.verify(this,extraTypes)

/**
 * Verify a list of Modules
 *
 * @see Module.verify
 */
@KoinExperimentalAPI
fun List<Module>.verifyAll(extraTypes: List<KClass<*>> = listOf()) {
    forEach { module -> module.verify(extraTypes) }
}

/**
 * Verify API
 *
 * Help to check current factory of a Module
 */
@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
object Verify {

    internal val primitiveTypes = listOf(
        String::class, Int::class,Long::class,Double::class
    )

    internal val whiteList = arrayListOf<KClass<*>>().apply {
        addAll(primitiveTypes)
    }

    fun addExtraTypes(vararg kClass: KClass<*>) {
        whiteList.addAll(kClass)
    }

    fun addExtraTypes(kClassList: List<KClass<*>>) {
        whiteList.addAll(kClassList)
    }

    /**
     * Make a static verification about all declared classes constructors, to ensure they are all bound to an existing definition
     * Throws MissingDefinitionException if any definition is missing
     *
     * @param module the moduel to verify
     * @param extraTypes allow to declare extra type, to be bound above the existing definitions
     * @throws MissingKoinDefinitionException
     */
    fun verify(module : Module, extraTypes: List<KClass<*>> = listOf()) {
        val timer = Timer.start()

        val allModules = flatten(module.includedModules.toList()) + module
        val factories = allModules.flatMap { it.mappings.values.toList() }
        val extraKeys = (extraTypes + Verify.whiteList).map { it.getFullName() }

        val keys = allModules.flatMap { it.mappings.keys.toList() } + extraKeys

        println("Verifying Module '$this' ...")
        println("- index keys: ${keys.size}")
        val verified = arrayListOf<InstanceFactory<*>>()
        factories.forEach { factory ->
            if (factory !in verified) {
                verifyFactory(factory, keys)
                verified.add(factory)
            }
        }

        timer.stop()
        val time =
            if (timer.getTimeInMillis() < 1000) "${timer.getTimeInMillis()} ms" else "${timer.getTimeInSeconds()} sec"
        println("[SUCCESS] module '$this' has been verified in $time.")
    }

    private fun verifyFactory(factory: InstanceFactory<*>, keys: List<String>) {
        val beanDefinition = factory.beanDefinition
        println("|-> $beanDefinition")
        val allTypes = listOf(beanDefinition.primaryType) + beanDefinition.secondaryTypes
        println("| bound types : $allTypes")
        allTypes.forEach { boundType ->
            val publicConstructors = boundType.constructors.filter { it.visibility == KVisibility.PUBLIC }
            publicConstructors.forEach { ctor ->
                verifyConstructor(ctor, boundType, keys, beanDefinition)
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
            val clazz = type.type.classifier as KClass<*>
            val classFullName = clazz.getFullName()
            val indexExists = keys.any { k -> k.contains(classFullName) }
            if (!indexExists) {
                System.err.println("* ----- > Missing type '${clazz.qualifiedName}' for class '${boundType.qualifiedName}' in definition '$beanDefinition'\nFix your Koin configuration or add extraTypes parameter: verify(extraTypes = listOf(${clazz.qualifiedName}::class))")
                throw MissingKoinDefinitionException("Missing type '${clazz.qualifiedName}' for class '${boundType.qualifiedName}' in definition '$beanDefinition'")
            } else {
                println("|- '$clazz'")
            }
        }
    }
}