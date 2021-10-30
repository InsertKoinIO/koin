package org.koin.compiler.metadata

import com.google.devtools.ksp.symbol.*
import org.koin.compiler.generator.KoinCodeGenerator
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Property
import org.koin.core.annotation.Qualifier

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
    val qualifierAnnotation = annotations.firstOrNull { a -> a.shortName.asString() == "Qualifier" }
    return qualifierAnnotation?.let {
        qualifierAnnotation.arguments.getValueArgument() ?: error("Can't get value for @Qualifier")
    }
}

fun List<KSValueParameter>.getConstructorParameters(): List<KoinMetaData.ConstructorParameter> {
    return map { param -> getConstructorParameter(param) }
}

private fun getConstructorParameter(param: KSValueParameter): KoinMetaData.ConstructorParameter {
    val firstAnnotation = param.annotations.firstOrNull()
    val annotationName = firstAnnotation?.shortName?.asString()
    val annotationValue = firstAnnotation?.arguments?.getValueArgument()
    KoinCodeGenerator.LOGGER.logging("annotation? $firstAnnotation  name:$annotationName  value:$annotationValue")
    return when (annotationName) {
        "${InjectedParam::class.simpleName}" -> KoinMetaData.ConstructorParameter.ParameterInject
        "${Property::class.simpleName}" -> KoinMetaData.ConstructorParameter.Property(annotationValue)
        "${Qualifier::class.simpleName}" -> KoinMetaData.ConstructorParameter.Dependency(annotationValue)
        else -> KoinMetaData.ConstructorParameter.Dependency()
    }
}

internal fun List<KSValueArgument>.getValueArgument(): String? {
    return firstOrNull { a -> a.name?.asString() == "value" }?.value as? String?
}