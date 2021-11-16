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

class ComponentScanner(
    val logger: KSPLogger,
) {

    fun extractDefinition(element: KSAnnotated): KoinMetaData.Definition {
        logger.logging("definition(class) -> $element", element)
        val ksClassDeclaration = (element as KSClassDeclaration)
        val packageName = ksClassDeclaration.containingFile!!.packageName.asString()
        val className = ksClassDeclaration.simpleName.asString()
        val qualifier = ksClassDeclaration.getStringQualifier()
        val annotations = element.getKoinAnnotations()
        val scopeAnnotation = annotations.getScopeAnnotation()
        return if (scopeAnnotation != null){
            createDefinition(element, scopeAnnotation.second, ksClassDeclaration, scopeAnnotation.first, packageName, qualifier, className, annotations)
        } else {
            annotations.firstNotNullOf { (annotationName, annotation) ->
                createDefinition(element, annotation, ksClassDeclaration, annotationName, packageName, qualifier, className, annotations)
            }
        }
    }

    private fun createDefinition(
        element: KSAnnotated,
        annotation: KSAnnotation,
        ksClassDeclaration: KSClassDeclaration,
        annotationName: String,
        packageName: String,
        qualifier: String?,
        className: String,
        annotations: Map<String, KSAnnotation> = emptyMap()
    ): KoinMetaData.Definition.ClassDefinition {
        val declaredBindings = declaredBindings(annotation)
        val defaultBindings = ksClassDeclaration.superTypes.map { it.resolve().declaration }.toList()
        val allBindings: List<KSDeclaration> = if (declaredBindings?.isNotEmpty() == true) declaredBindings else defaultBindings
        val ctorParams = ksClassDeclaration.primaryConstructor?.parameters?.getConstructorParameters()

        return when (annotationName) {
            SINGLE.annotationName -> {
                createSingleDefinition(annotation, packageName, qualifier, className, ctorParams, allBindings)
            }
            SINGLETON.annotationName -> {
                createSingleDefinition(annotation, packageName, qualifier, className, ctorParams, allBindings)
            }
            FACTORY.annotationName -> {
                createClassDefinition(FACTORY,packageName, qualifier, className, ctorParams, allBindings)
            }
            KOIN_VIEWMODEL.annotationName -> {
                createClassDefinition(FACTORY,packageName, qualifier, className, ctorParams, allBindings)
            }
            SCOPE.annotationName -> {
                val scopeData : KoinMetaData.Scope = annotation.arguments.getScope()
                val extraAnnotationDefinition = getExtraScopeAnnotation(annotations)
                val extraAnnotation = annotations[extraAnnotationDefinition?.annotationName]
                val extraDeclaredBindings = extraAnnotation?.let { declaredBindings(it) }
                val extraScopeBindings = if(extraDeclaredBindings?.isNotEmpty() == true) extraDeclaredBindings else allBindings
                createClassDefinition(extraAnnotationDefinition ?: SCOPE,packageName, qualifier, className, ctorParams, extraScopeBindings,scope = scopeData)
            }
            else -> error("Unknown annotation type: $annotationName")
        }
    }

    private fun createSingleDefinition(
        annotation: KSAnnotation,
        packageName: String,
        qualifier: String?,
        className: String,
        ctorParams: List<KoinMetaData.ConstructorParameter>?,
        allBindings: List<KSDeclaration>
    ): KoinMetaData.Definition.ClassDefinition {
        val createdAtStart: Boolean =
            annotation.arguments.firstOrNull { it.name?.asString() == "createdAtStart" }?.value as Boolean? ?: false
        return createClassDefinition(SINGLE, packageName, qualifier, className, ctorParams, allBindings, isCreatedAtStart = createdAtStart)
    }

    private fun createClassDefinition(
        keyword : DefinitionAnnotation,
        packageName: String,
        qualifier: String?,
        className: String,
        ctorParams: List<KoinMetaData.ConstructorParameter>?,
        allBindings: List<KSDeclaration>,
        isCreatedAtStart : Boolean? = null,
        scope: KoinMetaData.Scope? = null,
    ) = KoinMetaData.Definition.ClassDefinition(
        packageName = packageName,
        qualifier = qualifier,
        isCreatedAtStart = isCreatedAtStart,
        className = className,
        constructorParameters = ctorParams ?: emptyList(),
        bindings = allBindings,
        keyword = keyword,
        scope = scope
    )
}