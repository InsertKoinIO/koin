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
        logger.logging("definition(class) qualifier -> $qualifier", element)

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
        logger.logging("definition(class) bindings ...", element)
        val declaredBindingsTypes = annotation.arguments.firstOrNull { it.name?.asString() == "binds" }?.value as? List<KSType>?
        val declaredBindings = declaredBindingsTypes?.map { it.declaration }
        val defaultBindings = ksClassDeclaration.superTypes.map { it.resolve().declaration }.toList()
        val allBindings = if (declaredBindings?.isNotEmpty() == true) declaredBindings else defaultBindings
        logger.logging("definition(class) bindings -> $allBindings", element)

        val ctorParams = ksClassDeclaration.primaryConstructor?.parameters?.getConstructorParameters()
        logger.logging("definition(class) ctor -> $ctorParams", element)

        return when (annotationName) {
            SINGLE.annotationName -> {
                val createdAtStart: Boolean = annotation.arguments.firstOrNull { it.name?.asString() == "createdAtStart" }?.value as Boolean? ?: false
                createClassDefinition(SINGLE,packageName, qualifier, className, ctorParams, allBindings, isCreatedAtStart = createdAtStart)
            }
            FACTORY.annotationName -> {
                createClassDefinition(FACTORY,packageName, qualifier, className, ctorParams, allBindings)
            }
            KOIN_VIEWMODEL.annotationName -> {
                createClassDefinition(FACTORY,packageName, qualifier, className, ctorParams, allBindings)
            }
            SCOPE.annotationName -> {
                // TODO Any other annotation?
                logger.warn("other annotations: ${annotations.keys}",element)
                val scopeData : KoinMetaData.Scope = annotation.arguments.getScope()
                logger.logging("definition(class) scope -> $$scopeData", element)
                createClassDefinition(SCOPE,packageName, qualifier, className, ctorParams, allBindings,scope = scopeData)
            }
            else -> error("Unknown annotation type: $annotationName")
        }
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