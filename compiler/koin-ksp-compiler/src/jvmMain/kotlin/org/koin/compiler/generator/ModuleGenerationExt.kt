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
import org.koin.compiler.generator.*
import org.koin.compiler.metadata.KoinMetaData
import java.io.OutputStream

fun OutputStream.generateFieldModule(definitions: List<KoinMetaData.Definition>) {
    KoinGenerator.LOGGER.logging("- generate field definitions: $definitions ...")
    val classDefinitions = definitions.filterIsInstance<KoinMetaData.Definition.ClassDefinition>()
    // TODO optimize group/split
    val standardDefinitions = classDefinitions.filter { it.isNotScoped() }.toSet()
    val scopeDefinitions = classDefinitions.filter { it.isScoped() }.toSet()

    KoinGenerator.LOGGER.logging("- found defs: ${standardDefinitions.size}")
    KoinGenerator.LOGGER.logging("- found scope defs: ${scopeDefinitions.size}")
    standardDefinitions.forEach { def -> generateClassDeclarationDefinition(def) }

    scopeDefinitions
        .groupBy { it.scope }
        .forEach { (scope, definitions) ->
            KoinGenerator.LOGGER.logging("generate scope $scope")
            appendText(generateScope(scope!!))
            definitions.forEach { generateClassDeclarationDefinition(it as KoinMetaData.Definition.ClassDefinition) }
            // close scope
            appendText("\n\t\t\t\t}")
        }
    KoinGenerator.LOGGER.logging("- generate field -")
}

fun generateClassModule(classFile: OutputStream, module: KoinMetaData.Module) {
    classFile.appendText(MODULE_HEADER)
    if (module.definitions.any { it.qualifier != null || it.scope != null }) {
        classFile.appendText(MODULE_HEADER_STRING_QUALIFIER)
    }
    classFile.appendText(module.definitions.generateImports())

    val generatedField = "${module.name}Module"
    val classModule = "${module.packageName}.${module.name}"
    classFile.appendText("\nval $generatedField = module {")

    if (module.definitions.any { it is KoinMetaData.Definition.FunctionDefinition }) {
        classFile.appendText("\n\t\t\t\tval moduleInstance = $classModule()")
    }

    val standardDefinitions = module.definitions.filter { it.isNotScoped() }

    KoinGenerator.LOGGER.logging("generate - definitions")

    standardDefinitions.forEach {
        when (it) {
            is KoinMetaData.Definition.FunctionDefinition -> classFile.generateFunctionDeclarationDefinition(it)
            is KoinMetaData.Definition.ClassDefinition -> classFile.generateClassDeclarationDefinition(it)
        }
    }

    KoinGenerator.LOGGER.logging("generate - scopes")
    val scopeDefinitions = module.definitions.filter { it.isScoped() }
    scopeDefinitions
        .groupBy { it.scope }
        .forEach { (scope, definitions) ->
            KoinGenerator.LOGGER.logging("generate - scope $scope")
            classFile.appendText(generateScope(scope!!))
            definitions.forEach {
                when (it) {
                    is KoinMetaData.Definition.FunctionDefinition -> classFile.generateFunctionDeclarationDefinition(it)
                    is KoinMetaData.Definition.ClassDefinition -> classFile.generateClassDeclarationDefinition(it)
                }
            }
            // close scope
            classFile.appendText("\n\t\t\t\t}")
        }

    classFile.appendText("\n}")
    classFile.appendText("\nval $classModule.module : org.koin.core.module.Module get() = $generatedField")

    classFile.flush()
    classFile.close()
}

fun KoinGenerator.generateDefaultModuleForDefinitions(
    definitions: List<KoinMetaData.Definition>
) {
    definitions.firstOrNull { _ ->
        logger.logging("+ generate default module: ${definitions.size} ...")
        logger.logging("+ generate default module header ...")
        codeGenerator.getDefaultFile().apply {
            generateDefaultModuleHeader(definitions)
        }
        codeGenerator.getDefaultFile().generateFieldModule(definitions)
        logger.logging("+ generate default module header ...")
        codeGenerator.getDefaultFile().apply {
            generateDefaultModuleFooter()
        }
        logger.logging("+ generate default module +")
        true
    }
}

fun OutputStream.generateDefaultModuleHeader(definitions: List<KoinMetaData.Definition> = emptyList()) {
    appendText(DEFAULT_MODULE_HEADER)
    if (definitions.any { it.qualifier != null || it.scope != null }) {
        appendText(MODULE_HEADER_STRING_QUALIFIER)
    }
    appendText(definitions.generateImports())
    appendText(DEFAULT_MODULE_FUNCTION)
}

fun OutputStream.generateDefaultModuleFooter() {
    appendText(DEFAULT_MODULE_FOOTER)
}

private fun List<KoinMetaData.Definition>.generateImports(): String {
    return mapNotNull { definition -> definition.keyword.import?.let { "import $it" } }.joinToString(separator = "\n", postfix = "\n")
}