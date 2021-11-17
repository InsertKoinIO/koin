package org.koin.compiler

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import org.koin.compiler.generator.KoinGenerator
import org.koin.compiler.metadata.KoinMetaData
import org.koin.compiler.scanner.KoinMetaDataScanner

class BuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val koinCodeGenerator = KoinGenerator(codeGenerator, logger)
    private val koinMetaDataScanner = KoinMetaDataScanner(logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val defaultModule = KoinMetaData.Module(
            packageName = "",
            name = "defaultModule"
        )
        logger.logging("Scan metadata ...")
        val (moduleMap, definitions) = koinMetaDataScanner.scanAllMetaData(resolver, defaultModule)

        logger.logging("Generate code ...")
        if (moduleMap.isNotEmpty()) {
            koinCodeGenerator.generateModules(moduleMap, defaultModule)
        } else {
            koinCodeGenerator.generateDefaultModule(definitions)
        }
        return emptyList()
    }
}

class BuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return BuilderProcessor(environment.codeGenerator, environment.logger)
    }
}