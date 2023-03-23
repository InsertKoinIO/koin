package org.koin.test.verify

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.IndexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.Module
import org.koin.core.module.flatten
import org.koin.ext.getFullName
import java.util.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility

@OptIn(KoinInternalApi::class)
class Verification(val module: Module, extraTypes: List<KClass<*>>) {

    private val allModules: Set<Module> = flatten(module.includedModules.toList()) + module
    private val factories: List<InstanceFactory<*>> = allModules.flatMap { it.mappings.values.toList() }
    private val extraKeys: List<String> = (extraTypes + Verify.whiteList).map { it.getFullName() }
    internal val definitionIndex: List<IndexKey> = allModules.flatMap { it.mappings.keys.toList() } + extraKeys
    private val verifiedFactories: HashMap<InstanceFactory<*>, List<KClass<*>>> = hashMapOf()

    fun verify() {
        factories.forEach { factory ->
            if (factory !in verifiedFactories.keys) {
                val injectedDependencies = verifyFactory(factory, definitionIndex)
                verifiedFactories[factory] = injectedDependencies
            }
        }
    }

    private fun verifyFactory(
        factory: InstanceFactory<*>,
        index: List<String>,
    ): List<KClass<*>> {
        val beanDefinition = factory.beanDefinition
        println("|-> $beanDefinition")
        val types = listOf(beanDefinition.primaryType) + beanDefinition.secondaryTypes
        println("| bound types : $types")

        return types.flatMap { type ->
            val constructors = type.constructors.filter { it.visibility == KVisibility.PUBLIC }
            constructors.flatMap { ctor -> verifyConstructor(ctor, type, index, beanDefinition) }
        }
    }

    private fun verifyConstructor(
        constructorFunction: KFunction<*>,
        classOrigin: KClass<*>,
        index: List<String>,
        beanDefinition: BeanDefinition<*>,
    ): List<KClass<*>> {
        val constructorParameters = constructorFunction.parameters
        println("| constructor: $classOrigin -> $constructorParameters")

        return constructorParameters.map { constructorParameter ->
            val ctorParamClass = (constructorParameter.type.classifier as KClass<*>)
            val ctorParamClassName = ctorParamClass.getFullName()

            val isDefinitionDeclared = index.any { k -> k.contains(ctorParamClassName) }

            val alreadyBoundFactory = verifiedFactories.keys.firstOrNull { ctorParamClass in listOf(it.beanDefinition.primaryType) + it.beanDefinition.secondaryTypes }
            val factoryDependencies = verifiedFactories[alreadyBoundFactory]
            val isCircular = factoryDependencies?.let { classOrigin in factoryDependencies } ?: false

            when {
                !isDefinitionDeclared -> {
                    System.err.println("* ----- > Missing type '${ctorParamClass.qualifiedName}' for class '${classOrigin.qualifiedName}' in definition '$beanDefinition'\nFix your Koin configuration or add extraTypes parameter: verify(extraTypes = listOf(${ctorParamClass.qualifiedName}::class))")
                    throw MissingKoinDefinitionException("Missing type '${ctorParamClass.qualifiedName}' for class '${classOrigin.qualifiedName}' in definition '$beanDefinition'")
                }
                isCircular -> {
                    throw CircularInjectionException("Circular type injection '${ctorParamClass.qualifiedName}' to '${classOrigin.qualifiedName}'")
                }
                else -> {
                    println("|- '$ctorParamClass'")
                    ctorParamClass
                }
            }
        }
    }
}