package org.koin.compiler.metadata

import com.google.devtools.ksp.symbol.KSDeclaration

sealed class KoinMetaData {

    data class Module(
        val packageName: String,
        val name: String,
        val definitions: MutableList<Definition> = mutableListOf(),
        val type: ModuleType = ModuleType.FIELD,
        val componentScan: ComponentScan? = null,
    ) : KoinMetaData() {
        data class ComponentScan(val packageName: String = "")

        fun acceptDefinition(defPackageName: String): Boolean {
            return when {
                componentScan == null -> false
                componentScan.packageName.isNotEmpty() -> defPackageName.contains(
                    componentScan.packageName,
                    ignoreCase = true
                )
                componentScan.packageName.isEmpty() -> defPackageName.contains(packageName, ignoreCase = true)
                else -> false
            }
        }
    }

    enum class ModuleType {
        FIELD, CLASS
    }

    sealed class Scope {
        data class ClassScope(val type: KSDeclaration) : Scope()
        data class StringScope(val name: String) : Scope()
    }

    sealed class Definition(
        val label: String,
        val parameters: List<ConstructorParameter>,
        val packageName: String,
        val qualifier: String? = null,
        val isCreatedAtStart: Boolean? = null,
        val keyword: DefinitionAnnotation,
        val bindings: List<KSDeclaration>,
        val scope: Scope? = null,
    ) : KoinMetaData() {

        fun isScoped(): Boolean = scope != null
        fun isNotScoped(): Boolean = !isScoped()
        fun isType(keyword: DefinitionAnnotation): Boolean = this.keyword == keyword

        class FunctionDefinition(
            packageName: String,
            qualifier: String?,
            isCreatedAtStart: Boolean? = null,
            keyword: DefinitionAnnotation,
            val functionName: String,
            parameters: List<ConstructorParameter> = emptyList(),
            bindings: List<KSDeclaration>,
            scope: Scope? = null
        ) : Definition(functionName, parameters, packageName, qualifier, isCreatedAtStart, keyword, bindings, scope)

        class ClassDefinition(
            packageName: String,
            qualifier: String?,
            isCreatedAtStart: Boolean? = null,
            keyword: DefinitionAnnotation,
            val className: String,
            val constructorParameters: List<ConstructorParameter> = emptyList(),
            bindings: List<KSDeclaration>,
            scope: Scope? = null
        ) : Definition(className, constructorParameters,packageName, qualifier, isCreatedAtStart, keyword, bindings, scope)
    }

    sealed class ConstructorParameter(val nullable: Boolean = false) {
        data class Dependency(val value: String? = null, val isNullable: Boolean = false) :
            ConstructorParameter(isNullable)

        data class ParameterInject(val isNullable: Boolean = false) : ConstructorParameter(isNullable)
        data class Property(val value: String? = null, val isNullable: Boolean = false) :
            ConstructorParameter(isNullable)
    }
}