package org.koin.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

/**
 * @author Fabio de Matos
 * @since 16/02/2021.
 */
open class TestProcessor : SymbolProcessor {
    override fun finish() {

    }

    override fun init(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ) {

    }

    override fun process(resolver: Resolver): List<KSAnnotated> {

        return listOf()
    }

}