package org.koin.test.verify

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.IndexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.Module
import org.koin.core.module.flatten
import org.koin.ext.getFullName
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility

@OptIn(KoinInternalApi::class)
class Verification(
    val module: Module,
    extraTypes: List<KClass<*>>,
    private val manualVerification: ((String) -> Boolean)?,
) {

    private val allModules: Set<Module> = flatten(module.includedModules.toList()) + module
    private val factories: List<InstanceFactory<*>> = allModules.flatMap { it.mappings.values.toList() }
    private val extraKeys: List<String> = (extraTypes + Verify.whiteList).map { it.getFullName() }
    internal val definitionIndex: List<IndexKey> = allModules.flatMap { it.mappings.keys.toList() } + extraKeys
    private val verifiedFactories: HashMap<InstanceFactory<*>, List<KClass<*>>> = hashMapOf()

    fun verify() {
        factories.forEach { factory ->
            if (factory !in verifiedFactories.keys) {
                val injectedDependencies = verifyFactory(factory, definitionIndex, manualVerification)
                verifiedFactories[factory] = injectedDependencies
            }
        }
    }

    private fun verifyFactory(
        factory: InstanceFactory<*>,
        index: List<String>,
        manualVerification: ((String) -> Boolean)?,
    ): List<KClass<*>> {
        val beanDefinition = factory.beanDefinition
        println("\n|-> definition $beanDefinition")
        if (beanDefinition.qualifier != null){
            println("| qualifier: ${beanDefinition.qualifier}")
        }

        val boundTypes = listOf(beanDefinition.primaryType) + beanDefinition.secondaryTypes
        println("| bind types: $boundTypes")

        val functionType = beanDefinition.primaryType
        val constructors = functionType.constructors.filter { it.visibility == KVisibility.PUBLIC }

        return constructors.flatMap { constructor ->
            verifyConstructor(
                constructor,
                functionType,
                index,
                beanDefinition,
                manualVerification,
            )
        }
    }

    private fun verifyConstructor(
        constructorFunction: KFunction<*>,
        classOrigin: KClass<*>,
        index: List<String>,
        beanDefinition: BeanDefinition<*>,
        manualVerification: ((String) -> Boolean)?,
    ): List<KClass<*>> {
        val constructorParameters = constructorFunction.parameters

        if (constructorParameters.isEmpty()){
            println("| no dependency to check")
        } else {
            println("| ${constructorParameters.size} dependencies to check")
        }

        return constructorParameters.map { constructorParameter ->
            val ctorParamClass = (constructorParameter.type.classifier as KClass<*>)
            val ctorParamClassName = ctorParamClass.getFullName()

            val isDefinitionDeclared = index.any { k -> k.contains(ctorParamClassName) } ||
                    manualVerification?.invoke(ctorParamClassName) == true
            val alreadyBoundFactory = verifiedFactories.keys.firstOrNull { ctorParamClass in listOf(it.beanDefinition.primaryType) + it.beanDefinition.secondaryTypes }
            val factoryDependencies = verifiedFactories[alreadyBoundFactory]
            val isCircular = factoryDependencies?.let { classOrigin in factoryDependencies } ?: false

            when {
                !isDefinitionDeclared -> {
                    val errorMessage = "Missing definition type '${ctorParamClass.qualifiedName}' in definition '$beanDefinition'"
                    System.err.println("* ----- > $errorMessage\nFix your Koin configuration or add extraTypes parameter to whitelist the type: verify(extraTypes = listOf(${ctorParamClass.qualifiedName}::class))")
                    throw MissingKoinDefinitionException(errorMessage)
                }

                isCircular -> {
                    val errorMessage = "Circular injection between '${ctorParamClass.qualifiedName}' and '${classOrigin.qualifiedName}'. Fix your Koin configuration"
                    System.err.println("* ----- > $errorMessage")
                    throw CircularInjectionException(errorMessage)
                }

                else -> {
                    println("|- dependency '$ctorParamClass' found ✅")
                    ctorParamClass
                }
            }
        }
    }
}
