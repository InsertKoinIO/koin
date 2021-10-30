import org.koin.compiler.generator.*
import org.koin.compiler.metadata.KoinMetaData
import java.io.OutputStream

fun OutputStream.generateFieldModule(definitions: List<KoinMetaData.Definition>) {
    KoinCodeGenerator.LOGGER.logging("- generate field definitions: $definitions ...")
    val classDefinitions = definitions.filterIsInstance<KoinMetaData.Definition.ClassDeclarationDefinition>()
    val standardDefinitions = classDefinitions.filter { it !is KoinMetaData.ScopeDefinition }.toSet()
    val scopeDefinitions = classDefinitions.filter { it is KoinMetaData.ScopeDefinition }.toSet()

    KoinCodeGenerator.LOGGER.logging("- found defs: ${standardDefinitions.size}")
    KoinCodeGenerator.LOGGER.logging("- found scope defs: ${scopeDefinitions.size}")
    standardDefinitions.forEach { def -> generateClassDeclarationDefinition(def) }

    scopeDefinitions.filterIsInstance<KoinMetaData.ScopeDefinition>()
        .groupBy { it.scope }
        .forEach { (scope, definitions) ->
            KoinCodeGenerator.LOGGER.logging("generate scope $scope")
            appendText(generateScope(scope))
            definitions.forEach { generateClassDeclarationDefinition(it as KoinMetaData.Definition.ClassDeclarationDefinition) }
            // close scope
            appendText("\n\t\t\t\t}")
        }
    KoinCodeGenerator.LOGGER.logging("- generate field -")
}

fun generateClassModule(classFile: OutputStream, module: KoinMetaData.Module) {
    classFile.appendText(MODULE_HEADER)
    classFile.appendText(module.definitions.generateImports())

    val generatedField = "${module.name}Module"
    val classModule = "${module.packageName}.${module.name}"
    classFile.appendText("\nval $generatedField = module {")
    classFile.appendText("\n\t\t\t\tval moduleInstance = $classModule()")

    val standardDefinitions = module.definitions.filter { it !is KoinMetaData.ScopeDefinition }

    KoinCodeGenerator.LOGGER.logging("generate - definitions")

    standardDefinitions.forEach {
        when (it) {
            is KoinMetaData.Definition.FunctionDeclarationDefinition -> classFile.generateFunctionDeclarationDefinition(it)
            is KoinMetaData.Definition.ClassDeclarationDefinition -> classFile.generateClassDeclarationDefinition(it)
        }
    }

    KoinCodeGenerator.LOGGER.logging("generate - scopes")
    val scopeDefinitions = module.definitions.filter { it is KoinMetaData.ScopeDefinition }
    scopeDefinitions.filterIsInstance<KoinMetaData.ScopeDefinition>().groupBy { it.scope }
        .forEach { (scope, definitions) ->
            KoinCodeGenerator.LOGGER.logging("generate - scope $scope")
            classFile.appendText(generateScope(scope))
            definitions.forEach {
                when (it) {
                    is KoinMetaData.Definition.FunctionDeclarationDefinition -> classFile.generateFunctionDeclarationDefinition(it)
                    is KoinMetaData.Definition.ClassDeclarationDefinition -> classFile.generateClassDeclarationDefinition(it)
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

fun KoinCodeGenerator.generateDefaultModuleForDefinitions(
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
    appendText(definitions.generateImports())
    appendText(DEFAULT_MODULE_FUNCTION)
}

fun OutputStream.generateDefaultModuleFooter() {
    appendText(DEFAULT_MODULE_FOOTER)
}

private fun List<KoinMetaData.Definition>.generateImports(): String {
    return mapNotNull { definition -> definition.keyword.import?.let { "import $it" } }.joinToString(separator = "\n", postfix = "\n")
}