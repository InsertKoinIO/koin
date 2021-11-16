/*
 * Copyright 2017-2022 the original author or authors.
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
package org.koin.compiler.scanner

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import org.koin.compiler.metadata.*

class ModuleScanner(
    val logger: KSPLogger
) {

    fun createClassModule(element: KSAnnotated): ModuleIndex {
        val declaration = (element as KSClassDeclaration)
        val modulePackage = declaration.containingFile?.packageName?.asString() ?: ""
        val componentScan =
            getComponentScan(declaration)

        val name = "$element"
        val moduleMetadata = KoinMetaData.Module(
            packageName = modulePackage,
            name = name,
            type = KoinMetaData.ModuleType.CLASS,
            componentScan = componentScan
        )

        val annotatedFunctions = declaration.getAllFunctions()
            .filter {
                it.annotations.map { a -> a.shortName.asString() }.any { a -> isValidAnnotation(a) }
            }
            .toList()

        val definitions = annotatedFunctions.mapNotNull { addDefinition(it) }
        moduleMetadata.definitions += definitions

        val moduleIndex = ModuleIndex(if (componentScan?.packageName?.isNotEmpty() == true) componentScan.packageName else modulePackage, moduleMetadata)
        return moduleIndex
    }

    private fun getComponentScan(declaration: KSClassDeclaration): KoinMetaData.Module.ComponentScan? {
        val componentScan = declaration.annotations.firstOrNull { it.shortName.asString() == "ComponentScan" }
        return componentScan?.let { a ->
            val value : String = a.arguments.firstOrNull { arg -> arg.name?.asString() == "value" }?.value as? String? ?: ""
            KoinMetaData.Module.ComponentScan(value)
        }
    }

    private fun addDefinition(element: KSAnnotated): KoinMetaData.Definition? {
        val ksFunctionDeclaration = (element as KSFunctionDeclaration)
        val packageName = ksFunctionDeclaration.containingFile!!.packageName.asString()
        val returnedType = ksFunctionDeclaration.returnType?.resolve()?.declaration?.simpleName?.toString()
        val qualifier = ksFunctionDeclaration.getStringQualifier()

        return returnedType?.let {
            val functionName = ksFunctionDeclaration.simpleName.asString()

            val annotations = element.getKoinAnnotations()
            val scopeAnnotation = annotations.getScopeAnnotation()

            return if (scopeAnnotation != null){
                declareDefinition(scopeAnnotation.first, scopeAnnotation.second, packageName, qualifier, functionName, ksFunctionDeclaration, annotations)
            } else {
                annotations.firstNotNullOf { (annotationName, annotation) ->
                    declareDefinition(annotationName, annotation, packageName, qualifier, functionName, ksFunctionDeclaration, annotations)
                }
            }
        }
    }

    private fun declareDefinition(
        annotationName: String,
        annotation: KSAnnotation,
        packageName: String,
        qualifier: String?,
        functionName: String,
        ksFunctionDeclaration: KSFunctionDeclaration,
        annotations: Map<String, KSAnnotation> = emptyMap()
    ): KoinMetaData.Definition.FunctionDefinition? {
        val allBindings = declaredBindings(annotation) ?: emptyList()
        val functionParameters = ksFunctionDeclaration.parameters.getConstructorParameters()

        return when (annotationName) {
            SINGLE.annotationName -> {
                createSingleDefinition(annotation, packageName, qualifier, functionName, functionParameters, allBindings)
            }
            SINGLETON.annotationName -> {
                createSingleDefinition(annotation, packageName, qualifier, functionName, functionParameters, allBindings)
            }
            FACTORY.annotationName -> {
                createFunctionDefinition(FACTORY,packageName,qualifier,functionName,functionParameters,allBindings)
            }
            KOIN_VIEWMODEL.annotationName -> {
                createFunctionDefinition(KOIN_VIEWMODEL,packageName,qualifier,functionName,functionParameters,allBindings)
            }
            SCOPE.annotationName -> {
                val scopeData : KoinMetaData.Scope = annotation.arguments.getScope()
                val extraAnnotation = getExtraScopeAnnotation(annotations)
                createFunctionDefinition(extraAnnotation ?: SCOPE,packageName,qualifier,functionName,functionParameters,allBindings,scope = scopeData)
            }
            else -> null
        }
    }

    private fun createSingleDefinition(
        annotation: KSAnnotation,
        packageName: String,
        qualifier: String?,
        functionName: String,
        functionParameters: List<KoinMetaData.ConstructorParameter>,
        allBindings: List<KSDeclaration>
    ): KoinMetaData.Definition.FunctionDefinition {
        val createdAtStart: Boolean =
            annotation.arguments.firstOrNull { it.name?.asString() == "createdAtStart" }?.value as Boolean?
                ?: false
        return createFunctionDefinition(
            SINGLE,
            packageName,
            qualifier,
            functionName,
            functionParameters,
            allBindings,
            isCreatedAtStart = createdAtStart
        )
    }

    private fun createFunctionDefinition(
        keyword : DefinitionAnnotation,
        packageName: String,
        qualifier: String?,
        functionName: String,
        parameters: List<KoinMetaData.ConstructorParameter>?,
        allBindings: List<KSDeclaration>,
        isCreatedAtStart : Boolean? = null,
        scope: KoinMetaData.Scope? = null,
    ) = KoinMetaData.Definition.FunctionDefinition(
        packageName = packageName,
        qualifier = qualifier,
        isCreatedAtStart = isCreatedAtStart,
        functionName = functionName,
        parameters = parameters ?: emptyList(),
        bindings = allBindings,
        keyword = keyword,
        scope = scope
    )
}