package org.koin.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

import java.io.OutputStream

/**
 * @author Fabio de Matos
 * @since 16/02/2021.
 */
open class TestProcessor : SymbolProcessor {

    private lateinit var logger: KSPLogger
    lateinit var codeGenerator: CodeGenerator

    private var logFile: OutputStream? = null
    var invoked = false

    private val targetList = mutableListOf<KSPropertyDeclaration>()

    override fun finish() {

        logFile = codeGenerator.createNewFile(Dependencies(false), "", "TestProcessor_finish", "log")
        emit("Finish")

        writeKoinModule(targetList)

        emit("Finish end")
        logFile?.close()
    }

    override fun init(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ) {
        this.codeGenerator = codeGenerator

        this.logger = logger
        logFile = codeGenerator.createNewFile(Dependencies(false), "", "TestProcessor_process", "log")

        emit("hello")
        emit("TestProcessor: init($options)", "")

    }

    private fun getKsTypeFromClass(resolver: Resolver, clazz: Class<out Any>): KSType? {

        return clazz
            .let {
                object : KSName {
                    override fun asString(): String = it.canonicalName
                    override fun getQualifier(): String = "Not important "
                    override fun getShortName(): String = it.simpleName
                }
            }
            .let {
                resolver.getClassDeclarationByName(it)
                    ?.asStarProjectedType()
            }
            .also {
                emit("Did it work? $it", "    ")
            }

    }

    override fun process(resolver: Resolver): List<KSAnnotated> {

        emit("TestProcessor: process", "")

        return resolver
            .getSymbolsWithAnnotation(KoinInject::class.java.canonicalName)
            .map {
                emit("Yep now what $it")
                it
            }
            .map {
                if (it is KSPropertyDeclaration) {
                    emit("It is property $it ")
                    targetList.add(it)
                }
                it
            }
            .map {
                it.accept(KoinVisitor(it, lambda = { property ->
                    emit("Found something for target $property / ${property.type}")
                    targetList
                        .add(property)
                }), Unit)
                it
            }

        logFile?.close()
    }

    private fun writeKoinModule(injectedFieldList: List<KSPropertyDeclaration>) {

        emit("Writing module: $injectedFieldList")

        val fileKt = codeGenerator.createNewFile(
            Dependencies(false),
            "org.koin.ksp.generated",
            "KoinKspModule",
            "kt"
        )

        fileKt.appendText("import org.koin.dsl.module\n")
//        fileKt.appendText("import org.koin.example.CoffeeMaker\n")
        fileKt.appendText("\n")
        fileKt.appendText("\n")
        fileKt.appendText("class KoinKspModule {\n")
        fileKt.appendText("val module = module { \n", identation = "\t")

        emit("Hello write koin")
        createFactories(fileKt, injectedFieldList)

        fileKt.appendText("}\n", identation = "\t")
        fileKt.appendText("}\n")
        fileKt.close()

        emit("Writing module done : $injectedFieldList")
    }

    private fun createFactories(
        fileKt: OutputStream,
        injectedFieldList: List<KSPropertyDeclaration>
    ) {

        emit("Factories now $injectedFieldList")

        injectedFieldList
            .associateBy { it.type }
            .values
            .map {
                emit("Factory for $it")

                createFactory(fileKt, it)

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

                "factory { $clazz($it) }"
            }
            .let {
                fileKt.appendText("// location $location\n", identation = "\t\t")
                fileKt.appendText("$it\n", identation = "\t\t")
            }

    }

    fun emit(s: String, indent: String = ".") {

        try {
            logger.info("message= $s")
            logFile?.appendText("$indent$s\n")
        } catch (t: Throwable) {
            println("Cannot write to log")
        }
    }


}


fun OutputStream.appendText(str: String, identation: String = "") {

    this.write((identation + str).toByteArray())
}