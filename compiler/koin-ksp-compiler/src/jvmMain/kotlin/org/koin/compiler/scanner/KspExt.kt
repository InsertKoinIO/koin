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

import com.google.devtools.ksp.symbol.*
import org.koin.compiler.metadata.KoinMetaData
import org.koin.compiler.metadata.isScopeAnnotation
import org.koin.compiler.metadata.isValidAnnotation
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Named
import org.koin.core.annotation.Property

fun KSAnnotated.getKoinAnnotation(): Pair<String, KSAnnotation>? {
    return try {
        val a = annotations.firstOrNull { a -> isValidAnnotation(a.shortName.asString()) }
        a?.let { Pair(a.shortName.asString(), a) }
    } catch (e: Exception) {
        null
    }
}

fun KSAnnotated.getKoinAnnotations(): Map<String, KSAnnotation> {
    return annotations
        .filter { isValidAnnotation(it.shortName.asString()) }
        .map { annotation -> Pair(annotation.shortName.asString(), annotation) }
        .toMap()
}

fun Map<String, KSAnnotation>.getScopeAnnotation(): Pair<String, KSAnnotation>? {
    return firstNotNullOfOrNull { (name, annotation) ->
        if (isScopeAnnotation(name)) Pair(name, annotation) else null
    }
}

fun List<KSValueArgument>.getScope(): KoinMetaData.Scope {
    val scopeKClassType: KSType? = firstOrNull { it.name?.asString() == "value" }?.value as? KSType
    val scopeStringType: String? = firstOrNull { it.name?.asString() == "name" }?.value as? String
    return scopeKClassType?.let {
        val type = it.declaration
        if (type.simpleName.asString() != "NoClass") {
            KoinMetaData.Scope.ClassScope(type)
        } else null
    }
        ?: scopeStringType?.let { KoinMetaData.Scope.StringScope(it) }
        ?: error("Scope annotation needs parameters: either type value or name")
}

fun KSAnnotated.getStringQualifier(): String? {
    val qualifierAnnotation = annotations.firstOrNull { a -> a.shortName.asString() == "Named" }
    return qualifierAnnotation?.let {
        qualifierAnnotation.arguments.getValueArgument() ?: error("Can't get value for @Named")
    }
}

fun List<KSValueParameter>.getConstructorParameters(): List<KoinMetaData.ConstructorParameter> {
    return map { param -> getConstructorParameter(param) }
}

private fun getConstructorParameter(param: KSValueParameter): KoinMetaData.ConstructorParameter {
    val firstAnnotation = param.annotations.firstOrNull()
    val annotationName = firstAnnotation?.shortName?.asString()
    val annotationValue = firstAnnotation?.arguments?.getValueArgument()
    val isNullable = param.type.resolve().isMarkedNullable
    return when (annotationName) {
        "${InjectedParam::class.simpleName}" -> KoinMetaData.ConstructorParameter.ParameterInject(isNullable)
        "${Property::class.simpleName}" -> KoinMetaData.ConstructorParameter.Property(annotationValue, isNullable)
        "${Named::class.simpleName}" -> KoinMetaData.ConstructorParameter.Dependency(annotationValue, isNullable)
        else -> KoinMetaData.ConstructorParameter.Dependency(isNullable = isNullable)
    }
}

internal fun List<KSValueArgument>.getValueArgument(): String? {
    return firstOrNull { a -> a.name?.asString() == "value" }?.value as? String?
}