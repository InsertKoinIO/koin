package org.koin.ksp

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Fabio de Matos
 * @since 16/02/2021.
 */
open class TestProcessor : SymbolProcessor {

    private lateinit var logger: KSPLogger
    lateinit var codeGenerator: CodeGenerator

    private var logFile: OutputStream? = null
    var invoked = false

    private val maxDepth = 10
    private val depthCount = AtomicInteger(0)

    private val rootDependencyList = mutableSetOf<KSPropertyDeclaration>()
    private val indirectDependencyList = mutableSetOf<KSClassDeclaration>()

    private val activityInjectors = mutableMapOf<KSClassDeclaration, KSPropertyDeclaration>()

    override fun finish() {

        logFile =
            codeGenerator.createNewFile(Dependencies(false), "", "TestProcessor_finish", "log")
        emit("Finish")

        writeKoinModule()
        InjectorWriter(codeGenerator).writeKoinInjectors(activityInjectors)

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
        logFile =
            codeGenerator.createNewFile(Dependencies(false), "", "TestProcessor_process", "log")

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

        resolver
            .getSymbolsWithAnnotation(KoinInject::class.java.canonicalName)
            .map {
                it.accept(KoinVisitor(it, lambda = { property ->
                    emit("Found something for target $property / ${property.type}")
                    rootDependencyList
                        .add(property)

                    val closestClass = property.parentDeclaration
                        ?.closestClassDeclaration()
                        ?.let {
                            emit("Closest class $it - $property")
                            activityInjectors.put(it, property)
                        }


                }), Unit)
            }

        depthCount.set(0)
        processIndirectDependencies(resolver, rootDependencyList)

        logFile?.close()

        return listOf()
    }

    /**
     * Recursively searches the target list for all dependencies of its dependencies.
     *
     * TODO: not recursive yet, only goes 1 level deep
     */
    private fun processIndirectDependencies(
        resolver: Resolver,
        targetList: Set<KSPropertyDeclaration>
    ) {

        if (depthCount.incrementAndGet() > maxDepth) {
            logger.error("Max depth of $maxDepth reached. Check for circular dependencies.")
        }

        targetList
            .also {

                it.map { injectedReference ->

                    injectedReference
                        .type
                        .resolve()
//                    .resolve()
//                    .declaration

                    val clazz = injectedReference.type.resolve().declaration
                        .let { it as? KSClassDeclaration }

                    val constructor = clazz?.getConstructors()
                        ?.firstOrNull()

                    val parameters =
                        constructor?.parameters
                            ?.map {
                                it.toString() + ":" + it.type + "__" + it.type.resolve().declaration.qualifiedName?.let { "${it.getQualifier()}.${it.getShortName()}" }
                            }

                    emit(
                        "Looking at constructors for $injectedReference - ${injectedReference.type} ${clazz} --- ${parameters}",
                        "---"
                    )
                }
            }
            .mapNotNull { injectedReference ->
                injectedReference
                    .type.resolve().declaration
                    .let { it as? KSClassDeclaration }
                    ?.getConstructors()
                    ?.firstOrNull()
                    ?.parameters
                    ?.map {
                        it
                            .type.resolve().declaration
                            .qualifiedName
                            ?.let { "${it.getQualifier()}.${it.getShortName()}" }
                    }
            }
            .flatten()
            .mapNotNull { it }
            .mapNotNull {
                resolver.getClassDeclarationByName(it)
            }
            .map {

                indirectDependencyList.add(it)
                emit("Look what I found ${it?.toString()}")
            }

        depthCount.decrementAndGet()

    }

    private fun writeKoinModule() {

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

    private fun createFactory(fileKt: OutputStream, injectedReference: KSClassDeclaration) {

        val clazz = injectedReference
            .superTypes // we need a list of subclasses of injectedReference
            .also {
                emit("Trying to find implementation of $injectedReference in $it ")
            }


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
//                fileKt.appendText("$it\n", identation = "\t\t")
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

                "single (override=true){ $clazz($it) }"
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