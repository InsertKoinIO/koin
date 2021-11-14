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
    classFile.appendText(module.definitions.generateImports())

    val generatedField = "${module.name}Module"
    val classModule = "${module.packageName}.${module.name}"
    classFile.appendText("\nval $generatedField = module {")
    classFile.appendText("\n\t\t\t\tval moduleInstance = $classModule()")

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
    appendText(definitions.generateImports())
    appendText(DEFAULT_MODULE_FUNCTION)
}

fun OutputStream.generateDefaultModuleFooter() {
    appendText(DEFAULT_MODULE_FOOTER)
}

private fun List<KoinMetaData.Definition>.generateImports(): String {
    return mapNotNull { definition -> definition.keyword.import?.let { "import $it" } }.joinToString(separator = "\n", postfix = "\n")
}