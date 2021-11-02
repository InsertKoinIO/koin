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
                componentScan.packageName.isNotEmpty() -> defPackageName.contains(componentScan.packageName, ignoreCase = true)
                componentScan.packageName.isEmpty() -> defPackageName.contains(packageName, ignoreCase = true)
                else -> false
            }
        }
    }

    sealed class Scope {
        data class ClassScope(val type: KSDeclaration) : Scope()
        data class StringScope(val name: String) : Scope()
    }
    interface ScopeDefinition {
        val scope: KoinMetaData.Scope
    }

    sealed class Definition(
        val packageName: String,
        val qualifier: String? = null,
        val keyword: DefinitionAnnotation,
        val bindings: List<KSDeclaration>,
        val isAndroidDefinition: Boolean = false,
    ) : KoinMetaData() {

        sealed class FunctionDeclarationDefinition(
            packageName: String,
            qualifier: String?,
            keyword: DefinitionAnnotation,
            val functionName: String,
            val parameters: List<ConstructorParameter> = emptyList(),
            bindings: List<KSDeclaration>
        ) : Definition(packageName, qualifier, keyword, bindings) {

            class Single(
                packageName: String,
                qualifier: String?,
                functionName: String,
                functionParameters: List<ConstructorParameter> = emptyList(),
                val createdAtStart: Boolean = false,
                bindings: List<KSDeclaration>
            ) : FunctionDeclarationDefinition(
                packageName,
                qualifier,
                SINGLE,
                functionName,
                functionParameters,
                bindings
            )

            open class Factory(
                packageName: String,
                qualifier: String?,
                functionName: String,
                functionParameters: List<ConstructorParameter> = emptyList(),
                bindings: List<KSDeclaration>,
                _keyword: DefinitionAnnotation = FACTORY
            ) : FunctionDeclarationDefinition(packageName, qualifier, _keyword, functionName, functionParameters, bindings)

            class ViewModel(
                packageName: String,
                qualifier: String?,
                functionName: String,
                functionParameters: List<ConstructorParameter> = emptyList(),
                bindings: List<KSDeclaration>,
                _keyword: DefinitionAnnotation = KOIN_VIEWMODEL
            ) : Factory(packageName, qualifier, functionName, functionParameters, bindings, _keyword)

            open class Scope(
                packageName: String,
                qualifier: String?,
                functionName: String,
                functionParameters: List<ConstructorParameter> = emptyList(),
                bindings: List<KSDeclaration>,
                _keyword: DefinitionAnnotation = SCOPE,
                override val scope: KoinMetaData.Scope
            ) : ScopeDefinition, FunctionDeclarationDefinition(packageName, qualifier, _keyword, functionName, functionParameters, bindings)
        }

        sealed class ClassDeclarationDefinition(
            packageName: String,
            qualifier: String?,
            keyword: DefinitionAnnotation,
            val className: String,
            val constructorParameters: List<ConstructorParameter> = emptyList(),
            bindings: List<KSDeclaration>
        ) : Definition(packageName, qualifier, keyword, bindings) {

            class Single(
                packageName: String,
                qualifier: String?,
                className: String,
                constructorParameters: List<ConstructorParameter> = emptyList(),
                val createdAtStart: Boolean,
                bindings: List<KSDeclaration>
            ) : ClassDeclarationDefinition(packageName, qualifier, SINGLE, className, constructorParameters, bindings)

            open class Factory(
                packageName: String,
                qualifier: String?,
                className: String,
                constructorParameters: List<ConstructorParameter> = emptyList(),
                bindings: List<KSDeclaration>,
                _keyword: DefinitionAnnotation = FACTORY,
            ) : ClassDeclarationDefinition(packageName, qualifier, _keyword, className, constructorParameters, bindings)

            class ViewModel(
                packageName: String,
                qualifier: String?,
                className: String,
                constructorParameters: List<ConstructorParameter> = emptyList(),
                bindings: List<KSDeclaration>,
                _keyword: DefinitionAnnotation = KOIN_VIEWMODEL,
            ) : Factory(packageName, qualifier, className, constructorParameters, bindings, _keyword)

            open class Scope(
                packageName: String,
                qualifier: String?,
                className: String,
                constructorParameters: List<ConstructorParameter> = emptyList(),
                bindings: List<KSDeclaration>,
                _keyword: DefinitionAnnotation = SCOPE,
                override val scope: KoinMetaData.Scope
            ) : ScopeDefinition, ClassDeclarationDefinition(packageName, qualifier, _keyword, className, constructorParameters, bindings)
        }
    }

    enum class ModuleType {
        FIELD, CLASS
    }

    sealed class ConstructorParameter {
        data class Dependency(val value: String? = null) : ConstructorParameter()
        object ParameterInject : ConstructorParameter()
        data class Property(val value: String? = null) : ConstructorParameter()
    }
}