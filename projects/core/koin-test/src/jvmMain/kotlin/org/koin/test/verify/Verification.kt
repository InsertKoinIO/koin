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

import org.koin.core.annotation.KoinExperimentalAPI
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

/**
 *
 */
@OptIn(KoinInternalApi::class, KoinExperimentalAPI::class)
class Verification(val module: Module, extraTypes: List<KClass<*>>, injections: List<ParameterTypeInjection>? = null) {

    private val allModules: Set<Module> = flatten(module.includedModules.toList()) + module
    private val factories: List<InstanceFactory<*>> = allModules.flatMap { it.mappings.values.toList() }
    private val extraKeys: List<String> = (extraTypes + Verify.whiteList).map { it.getFullName() }
    internal val definitionIndex: List<IndexKey> = allModules.flatMap { it.mappings.keys.toList() }
    private val verifiedFactories: HashMap<InstanceFactory<*>, List<KClass<*>>> = hashMapOf()
    private val parameterInjectionIndex: Map<String, List<String>> = injections?.associate { inj -> inj.targetType.getFullName() to inj.injectedTypes.map { it.getFullName() }.toList() }.orEmpty()

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
        println("\n|-> definition $beanDefinition")
        if (beanDefinition.qualifier != null) {
            println("| qualifier: ${beanDefinition.qualifier}")
        }

        val boundTypes = listOf(beanDefinition.primaryType) + beanDefinition.secondaryTypes
        println("| bind types: $boundTypes")

        val functionType = beanDefinition.primaryType
        val constructors = functionType.constructors.filter { it.visibility == KVisibility.PUBLIC }

        val verifications = constructors
            .flatMap { constructor ->
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
            val generateParameterInjection = "- Fix your Koin configuration -\n${generateFixDefinition(first)}\n${generateInjectionCode(beanDefinition, first)}"
            System.err.println("* ----- > $errorMessage\n$generateParameterInjection")
            throw MissingKoinDefinitionException(errorMessage)
        }
        verificationByStatus[VerificationStatus.CIRCULAR]?.let { list ->
            val errorMessage = "Circular injection between ${list.first()} and '${functionType.qualifiedName}'.\nFix your Koin configuration!"
            System.err.println("* ----- > $errorMessage")
            throw CircularInjectionException(errorMessage)
        }

        return verificationByStatus[VerificationStatus.OK]?.map {
            println("|- dependency '${it.name}' - ${it.type.qualifiedName} found!")
            it.type
        }.orEmpty()
    }

    private fun generateFixDefinition(first: VerifiedParameter): String {
        val className = first.type.qualifiedName
        return """
            1- add missing definition for type '$className'. like: singleOf(::$className)
            or
        """.trimIndent()
    }

    private fun generateInjectionCode(beanDefinition: BeanDefinition<*>, p: VerifiedParameter): String {
        val className = beanDefinition.primaryType.qualifiedName
        return """
            2- else define injection for '$className' with:
            module.verify(
                injections = injectedParameters(
                    definition<$className>(${p.type.qualifiedName}::class)
                )
            )
        """.trimIndent()
    }

    private fun verifyConstructor(
        constructorFunction: KFunction<*>,
        functionType: KClass<*>,
        index: List<String>,
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

            val hasDefinition = isClassInDefinitionIndex(index, ctorParamFullClassName)
            val isParameterInjected = isClassInInjectionIndex(functionType, ctorParamFullClassName)
            if (isParameterInjected) {
                println("| dependency '$ctorParamLabel' is injected")
            }
            val isWhiteList = ctorParamFullClassName in extraKeys
            if (isWhiteList) {
                println("| dependency '$ctorParamLabel' is whitelisted")
            }
            val isDefinitionDeclared = hasDefinition || isParameterInjected || isWhiteList

            val alreadyBoundFactory = verifiedFactories.keys.firstOrNull { ctorParamClass in listOf(it.beanDefinition.primaryType) + it.beanDefinition.secondaryTypes }
            val factoryDependencies = verifiedFactories[alreadyBoundFactory]
            val isCircular = factoryDependencies?.let { functionType in factoryDependencies } ?: false

            //TODO refactor to attach type / case of error
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

    private fun isClassInDefinitionIndex(index: List<String>, ctorParamFullClassName: String) =
        index.any { k -> k.contains(ctorParamFullClassName) }
}

data class VerifiedParameter(val name: String, val type: KClass<*>, val status: VerificationStatus) {
    override fun toString(): String = "[field:'$name' - type:'${type.qualifiedName}']"
}

enum class VerificationStatus {
    OK, MISSING, CIRCULAR
}
