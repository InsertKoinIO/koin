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
import com.google.devtools.ksp.symbol.KSDeclaration
import org.koin.compiler.generator.KoinGenerator.Companion.LOGGER
import org.koin.compiler.metadata.KoinMetaData
import org.koin.compiler.metadata.SINGLE
import java.io.OutputStream

fun OutputStream.generateDefinition(def: KoinMetaData.Definition, label : () -> String) {
    LOGGER.logging("generate $def")
    val param = def.parameters.generateParamFunction()
    val ctor = generateConstructor(def.parameters)
    val binds = generateBindings(def.bindings)
    val qualifier = def.qualifier.generateQualifier()
    val createAtStart = if (def.isType(SINGLE) && def.isCreatedAtStart == true) CREATED_AT_START else ""
    val defaultSpace = "\n\t\t\t\t"
    val space = if (def.isScoped()) defaultSpace + "\t" else defaultSpace
    appendText("$space${def.keyword.keyword}($qualifier$createAtStart) { ${param}${label()}$ctor } $binds")
}

fun OutputStream.generateFunctionDeclarationDefinition(def: KoinMetaData.Definition.FunctionDefinition) {
    generateDefinition(def){"moduleInstance.${def.functionName}"}
}


fun OutputStream.generateClassDeclarationDefinition(def: KoinMetaData.Definition.ClassDefinition) {
    generateDefinition(def){"${def.packageName}.${def.className}"}
}

const val CREATED_AT_START = ",createdAtStart=true"

fun List<KoinMetaData.ConstructorParameter>.generateParamFunction(): String {
    return if (any { it is KoinMetaData.ConstructorParameter.ParameterInject }) "params -> " else ""
}

fun String?.generateQualifier(): String = when {
    this == "\"null\"" -> "qualifier=null"
    this == "null" -> "qualifier=null"
    !this.isNullOrBlank() -> "qualifier=StringQualifier(\"$this\")"
    else -> "qualifier=null"
}

fun generateBindings(bindings: List<KSDeclaration>): String {
    return when {
        bindings.isEmpty() -> ""
        bindings.size == 1 -> {
            val generateBinding = generateBinding(bindings.first())
            generateBinding?.let { "bind($generateBinding)" } ?: ""
        }
        else -> bindings.joinToString(prefix = "binds(arrayOf(", separator = ",", postfix = "))") { generateBinding(it) ?: "" }
    }
}

fun generateScope(scope: KoinMetaData.Scope): String {
    return when(scope){
        is KoinMetaData.Scope.ClassScope -> {
            val type = scope.type
            val packageName = type.containingFile!!.packageName.asString()
            val className = type.simpleName.asString()
            "\n\t\t\t\tscope<$packageName.$className> {"
        }
        is KoinMetaData.Scope.StringScope -> "\n\t\t\t\tscope(StringQualifier(\"${scope.name}\")) {"
    }
}

fun generateBinding(declaration: KSDeclaration): String? {
    val packageName = declaration.containingFile?.packageName?.asString()
    val className = declaration.simpleName.asString()
    return packageName?.let { "$packageName.$className::class" }
}

fun generateConstructor(constructorParameters: List<KoinMetaData.ConstructorParameter>): String {
    return constructorParameters.joinToString(prefix = "(", separator = ",", postfix = ")") { ctorParam ->
        val isNullable : Boolean = ctorParam.nullable
        when (ctorParam) {
            is KoinMetaData.ConstructorParameter.Dependency -> {
                val qualifier = ctorParam.value?.let { "qualifier=StringQualifier(\"${it}\")" } ?: ""
                if (!isNullable) "get($qualifier)" else "getOrNull($qualifier)"
            }
            is KoinMetaData.ConstructorParameter.ParameterInject -> if (!isNullable) "params.get()" else "params.getOrNull()"
            is KoinMetaData.ConstructorParameter.Property -> if (!isNullable) "getProperty(\"${ctorParam.value}\")" else "getPropertyOrNull(\"${ctorParam.value}\")"
        }
    }
}