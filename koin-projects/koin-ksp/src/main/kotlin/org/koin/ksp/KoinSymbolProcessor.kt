package org.koin.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import java.io.OutputStream

class KoinSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {


    companion object {
        private var count = 0
    }

    private val logFile: OutputStream


    private val visitor = KoinVisitor2(::emit)

    init {

        logFile = codeGenerator.createNewFile(
            Dependencies(false),
            packageName = "org.koin.ksp.generated",
            fileName = "KoinSymbolProcessor.scanner",
            extensionName = "log"
        )

        emit("Options = $options")

    }

    fun emit(s: String, indent: String = "\t") {

        try {
            logFile.appendText("$indent$s\n")
        } catch (t: Throwable) {
            logger.exception(RuntimeException("Whateverrrr", t))
        }
        logger.info(s)
    }

    override fun finish() {
        logFile.close()


        val logFile2 = codeGenerator.createNewFile(
            Dependencies(false),
            packageName = "org.koin.ksp.generated",
            fileName = "KoinSymbolProcessor.writer",
            extensionName = "log"
        )

        KoinModuleWriter(logFile2)
            .writeKoinModule(
                codeGenerator,
                rootDependencyList = mutableSetOf(),
                indirectDependencyList = visitor.classSet
            )

        super.finish()
    }

    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {


        if (invoked) {
            return emptyList()
        }
        invoked = true

        return findSymbols(resolver)

    }

    private fun findSymbols(resolver: Resolver): List<KSAnnotated> {

        return resolver
            .getSymbolsWithAnnotation(

                annotationName = KoinInject::class.java.canonicalName,
                inDepth = true
            )
            .filter { it is KSClassDeclaration && it.validate() }
            .also {
                it
                    .forEach {

                        emit(
                            "++++++++++++++++++++++++ Process symbol $it ${it.annotations} - ",
                            ""
                        )
                    }
            }
            .map {
                it.accept(visitor, Unit)
                it
            }
            .toList()
    }


}

fun OutputStream.appendText(str: String, identation: String = "") {

    this.write((identation + str).toByteArray())
}

fun KSClassDeclaration.toFullName(): String {

    return "${this.toPackageName()}.${this.qualifiedName?.getShortName()}"

}

fun KSClassDeclaration.toPackageName(): String {

    return this.qualifiedName?.getQualifier() ?: ""

}