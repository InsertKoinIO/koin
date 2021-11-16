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