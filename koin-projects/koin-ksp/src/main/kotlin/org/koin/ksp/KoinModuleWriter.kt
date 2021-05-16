package org.koin.ksp

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import java.io.OutputStream

/**
 * Writes a koin module
 * @author Fabio de Matos
 * @since 14/05/2021.
 */
class KoinModuleWriter(private val logFile: OutputStream?) {


    fun writeKoinModule(
        codeGenerator: CodeGenerator,
        rootDependencyList: MutableSet<KSPropertyDeclaration>,
        indirectDependencyList: MutableSet<KSClassDeclaration>
    ) {

        emit("Writing module: 1- $rootDependencyList")
        emit("Writing module: 2- $indirectDependencyList")

        val fileKt = codeGenerator.createNewFile(
            Dependencies(false),
            "org.koin.ksp.generated",
            "KoinKspModule",
            "kt"
        )

        fileKt.appendText("package org.koin.ksp.generated\n")
        fileKt.appendText("\n")
        fileKt.appendText("\n")
        fileKt.appendText("import org.koin.dsl.module\n")
        fileKt.appendText("\n")
        fileKt.appendText("\n")
        fileKt.appendText("class KoinKspModule {\n")
        fileKt.appendText("val module = module { \n", identation = "\t")

        emit("Hello write koin")
        createFactories(fileKt, rootDependencyList, indirectDependencyList)

        fileKt.appendText("}\n", identation = "\t")
        fileKt.appendText("}\n")
        fileKt.close()

        emit("Writing module done : $rootDependencyList")
    }

    private fun createFactories(
        fileKt: OutputStream,
        injectedFieldList: Set<KSPropertyDeclaration>,
        injectedIndirectClasses: Set<KSClassDeclaration>
    ) {

        emit("Factories now $injectedFieldList")

        injectedFieldList
            .map {
                emit("Factory for $it")

                createFactory(fileKt, it)

            }

        injectedIndirectClasses
            .map {
                createFactory(fileKt, it)
            }
    }

    /**
     * Creates a singleton out of [injectedReference]
     */
    private fun createFactory(fileKt: OutputStream, injectedReference: KSClassDeclaration) {


        val fullClassName = injectedReference
            .closestClassDeclaration()
            .let {
                "${it?.qualifiedName?.getQualifier()}.$it"
            }

        val location = injectedReference.location

        injectedReference
            .closestClassDeclaration()
            ?.primaryConstructor
            ?.parameters
            ?.size
            ?.let {
                Array(it) {
                    "get()"
                }
            }
            ?.joinToString(separator = ", ")
            .let {

                "single { $fullClassName($it) }"
            }
            .let {
                fileKt.appendText("\n\n")
                fileKt.appendText("// location $location\n", identation = "\t\t")
                fileKt.appendText("$it\n", identation = "\t\t")
            }


        createSuperFactory(fileKt, injectedReference)
    }

    /**
     * Creates a factory out of the supertypes of [injectedReference]
     */
    private fun createSuperFactory(fileKt: OutputStream, injectedReference: KSClassDeclaration) {

        injectedReference
            .superTypes
            .mapNotNull {
                emit("Super types for $injectedReference = $it", "\t")
                it.resolve().declaration.closestClassDeclaration()
            }
            .map {

                emit("Factory with $injectedReference", "\t\t")
                "factory<${it.toFullName()}> { get<${injectedReference.toFullName()}>() } "
            }
            .map {
                fileKt.appendText("$it\n", identation = "\t\t")
            }
    }

    private fun createFactory(fileKt: OutputStream, injectedReference: KSPropertyDeclaration) {

        val clazz = injectedReference
            .type
            .resolve()
            .declaration
            .qualifiedName
            ?.let { "${it.getQualifier()}.${it.getShortName()}" }


        val location = injectedReference.location


        injectedReference.javaClass
            .constructors
            .first()
            .parameterCount
            .let {
                Array(it) {
                    "get()"
                }
            }
            .joinToString(separator = ", ")
            .let {

                "single(){ $clazz($it) }"
            }
            .let {
                fileKt.appendText("\n")
                fileKt.appendText("// llocation $location\n", identation = "\t\t")
                fileKt.appendText("$it\n", identation = "\t\t")
            }

    }


    fun emit(s: String, indent: String = ".") {

        try {
//            logger.info("message= $s")
            logFile?.appendText("$indent$s\n")
        } catch (t: Throwable) {
            println("Cannot write to log")
        }
    }


}