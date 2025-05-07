/*
 * Copyright 2017-Present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.test.verify

import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.annotation.Provided
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.IndexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.Module
import org.koin.core.module.flatten
import org.koin.ext.getFullName
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility

/**
 *
 */
@OptIn(KoinInternalApi::class, KoinExperimentalAPI::class)
class Verification(val module: Module? = null, extraTypes: List<KClass<*>> = emptyList(), injections: List<ParameterTypeInjection>? = null) {

    private var allModules: Set<Module> = module?.let { flatten(module.includedModules.toList()) + module } ?: emptySet()
    private var factories: Set<InstanceFactory<*>> = allModules.flatMap { it.mappings.values.toList() }.toSet()
    private var definitionIndex: Set<IndexKey> = allModules.flatMap { it.mappings.keys.toList() }.toSet()
    private var extraKeys: HashSet<String> = (extraTypes + Verify.whiteList).map { it.getFullName() }.toHashSet()
    private var parameterInjectionIndex: Map<String, List<String>> = injections?.associate { inj -> inj.targetType.getFullName() to inj.injectedTypes.map { it.getFullName() }.toList() }.orEmpty()

    private val verifiedFactories: HashMap<InstanceFactory<*>, List<KClass<*>>> = hashMapOf()

    fun verify() {

        // index duplication
        val allIndex = allModules.flatMap { it.mappings.toList() }
        allIndex.forEach { (k,v) ->
            val last = allIndex.last { it.first == k }
            if (last.second != v){
                println("\n+ definition override detected on index '$k' - from ${v.beanDefinition} over ${last.second.beanDefinition}")
            }
        }

        factories.forEach { factory ->
            if (factory !in verifiedFactories.keys) {
                val injectedDependencies = verifyFactory(factory, definitionIndex)
                verifiedFactories[factory] = injectedDependencies
            }
        }
    }

    private fun verifyFactory(
        factory: InstanceFactory<*>,
        index: Set<String>,
    ): List<KClass<*>> {
        val beanDefinition = factory.beanDefinition
        println("\n|-> definition $beanDefinition")
        if (beanDefinition.qualifier != null) {
            println("| qualifier: ${beanDefinition.qualifier}")
        }

        val boundTypes = listOf(beanDefinition.primaryType) + beanDefinition.secondaryTypes
        println("| bind types: $boundTypes")

        val functionType = beanDefinition.primaryType
        val constructors = functionType.constructors.filter { it.visibility == KVisibility.PUBLIC }
        println("| found constructors: ${constructors.size}")

        val verifications = constructors
            .flatMapIndexed { i,constructor ->
                println("| constructor($i) - $constructor")

                verifyConstructor(
                    constructor,
                    functionType,
                    index
                )
            }

        val verificationByStatus = verifications.groupBy { it.status }
        verificationByStatus[VerificationStatus.MISSING]?.let { list ->
            val first = list.first()
            val errorMessage = "Missing definition for '$first' in definition '$beanDefinition'."
            val generateParameterInjection = "--- Fix your Koin configuration ---\n${generateFixDefinition(first)}\n${generateInjectionCode(beanDefinition, first)}\n---"
            System.err.println("* ----- > $errorMessage\n$generateParameterInjection")
            throw MissingKoinDefinitionException(errorMessage)
        }
        verificationByStatus[VerificationStatus.CIRCULAR]?.let { list ->
            val errorMessage = "Circular injection between ${list.first()} and '${functionType.qualifiedName}'.\nFix your Koin configuration!"
            System.err.println("* ----- > $errorMessage")
            throw CircularInjectionException(errorMessage)
        }

        return verificationByStatus[VerificationStatus.OK]?.map {
            println("|- dependency '${it.name}' - ${it.type.qualifiedName} is verified!")
            it.type
        }.orEmpty()
    }

    private fun generateFixDefinition(first: VerifiedParameter): String {
        val className = first.type.qualifiedName
        return """
            -1- Missing Definition? Add missing definition for like: 'singleOf(::$className)'
            or
        """.trimIndent()
    }

    private fun generateInjectionCode(beanDefinition: BeanDefinition<*>, p: VerifiedParameter): String {
        val className = beanDefinition.primaryType.qualifiedName
        return """
            -2- Injected Parameter? Annotate property with @${InjectedParam::class.simpleName} like: '@${InjectedParam::class.simpleName} ${p.name} : ${p.type.qualifiedName}'
            or
            -3- Dynamically Provided? Annotate property with @${Provided::class.simpleName} like: '@${Provided::class.simpleName} ${p.name} : ${p.type.qualifiedName}'
            Or Define type '$className' with 'module.verify(injections = injectedParameters(definition<$className>(${p.type.qualifiedName}::class)))'
        """.trimIndent()
    }

    private fun verifyConstructor(
        constructorFunction: KFunction<*>,
        functionType: KClass<*>,
        index: Set<String>,
    ): List<VerifiedParameter> {
        val constructorParameters = constructorFunction.parameters

        if (constructorParameters.isEmpty()) {
            println("| no dependency to check")
        } else {
            println("| ${constructorParameters.size} dependencies to check")
        }

        return constructorParameters.map { constructorParameter ->
            val ctorParamLabel = constructorParameter.name ?: ""
            val ctorParamClass = (constructorParameter.type.classifier as KClass<*>)
            val ctorParamFullClassName = ctorParamClass.getFullName()

            var hasDefinition = isClassInDefinitionIndex(index, ctorParamFullClassName)
            val isParameterInjected = constructorParameter.annotations.any { it.annotationClass == InjectedParam::class } || isClassInInjectionIndex(functionType, ctorParamFullClassName)
            if (isParameterInjected) {
                println("| dependency '$ctorParamLabel' is tagged as dynamically injected as parameter")
            }
            var isWhiteList = ctorParamFullClassName in extraKeys
            if (isWhiteList) {
                println("| dependency '$ctorParamLabel' is whitelisted")
            }

            val isProvidedDynamically  = constructorParameter.annotations.any { it.annotationClass == Provided::class }
            if (!isWhiteList && isProvidedDynamically){
                System.err.println("* ----- > dependency '$ctorParamLabel' is tagged as provided dynamically!")
                extraKeys.add(ctorParamFullClassName)
                println("| dependency '$ctorParamLabel' is now whitelisted")
            }

            val isOptionalParameter = constructorParameter.isOptional

            val classifier = (constructorParameter.type.classifier as? KClass<*>)?.simpleName
            if (classifier == "Lazy" || classifier == "List"){
                val realType = constructorParameter.type.arguments.first().type?.classifier as? KClass<*>
                realType?.let {
                    val realTypeFullName = it.getFullName()
                    hasDefinition = isClassInDefinitionIndex(index, realTypeFullName)
                    isWhiteList = realTypeFullName in extraKeys
                }
            }

            val isFound = hasDefinition || isParameterInjected || isWhiteList || isProvidedDynamically
            if (isOptionalParameter) {
                if (!isFound){
                    System.err.println("* ----- > dependency '$ctorParamLabel' is optional, but no definition found in current configuration! It will be always null.")
                }
            }

            val isDefinitionDeclared = isFound || isOptionalParameter
            val alreadyBoundFactory = verifiedFactories.keys.firstOrNull { ctorParamClass in listOf(it.beanDefinition.primaryType) + it.beanDefinition.secondaryTypes }
            val factoryDependencies = verifiedFactories[alreadyBoundFactory]
            val isCircular = factoryDependencies?.let { functionType in factoryDependencies } == true

            when {
                !isDefinitionDeclared -> VerifiedParameter(ctorParamLabel, ctorParamClass, VerificationStatus.MISSING)
                isCircular -> VerifiedParameter(ctorParamLabel, ctorParamClass, VerificationStatus.CIRCULAR)
                else -> VerifiedParameter(ctorParamLabel, ctorParamClass, VerificationStatus.OK)
            }
        }
    }

    private fun isClassInInjectionIndex(
        classOrigin: KClass<*>,
        ctorParamFullClassName: String,
    ): Boolean {
        return parameterInjectionIndex[classOrigin.getFullName()]?.let { ctorParamFullClassName in it } ?: false
    }

    private fun isClassInDefinitionIndex(index: Set<String>, ctorParamFullClassName: String) =
        index.any { k -> k.contains(ctorParamFullClassName) }

    operator fun plus(v : Verification) : Verification {
        this.allModules = allModules + v.allModules
        this.factories = factories + v.factories
        this.definitionIndex = definitionIndex + v.definitionIndex
        this.extraKeys.addAll(v.extraKeys)
        this.parameterInjectionIndex = parameterInjectionIndex + v.parameterInjectionIndex
        return this
    }
}

data class VerifiedParameter(val name: String, val type: KClass<*>, val status: VerificationStatus) {
    override fun toString(): String = "[field:'$name' - type:'${type.qualifiedName}']"
}

enum class VerificationStatus {
    OK, MISSING, CIRCULAR
}
