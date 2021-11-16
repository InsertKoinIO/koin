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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Definition

            if (label != other.label) return false
            if (packageName != other.packageName) return false
            if (scope != other.scope) return false

            return true
        }

        override fun hashCode(): Int {
            var result = label.hashCode()
            result = 31 * result + packageName.hashCode()
            result = 31 * result + (scope?.hashCode() ?: 0)
            return result
        }

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