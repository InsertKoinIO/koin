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

        KoinModuleWriter(
            logFile
        ).writeKoinModule(
            codeGenerator,
            rootDependencyList,
            indirectDependencyList
        )

        emit("Injector over ${activityInjectors.keys}", "++")
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

//        FindKoinModulesUseCase(logFile)
//            .invoke(resolver)

        resolver
            .getSymbolsWithAnnotation(KoinInject::class.java.canonicalName)
            .map {
                it.accept(
                    KoinVisitor(
                        lambdaClass = { clazz ->
                            emit("Found something for target ${clazz.closestClassDeclaration()}")

                            indirectDependencyList.add(clazz)

                        },
                        lambdaProperty = { property ->

                            property
                                .annotations
                                .map {
//                                    emit(
//                                        "$property annotation $it  //  ${it.shortName.getQualifier()} == ${injectAnnotation?.simpleName?.getQualifier()} == ${KoinInject::class.java.simpleName} === @${KoinInject::class.java.simpleName}",
//                                        ",,,"
//                                    )
                                    it
                                }
                                .filter {
                                    val a = it.shortName.asString()
                                    val b = "${KoinInject::class.java.simpleName}"

//                                    emit("Really $a == $b -> ${a == b}")
                                    a == b
                                }
                                .map {
                                    emit("$property annotation $it ", "+++")
                                    val closestClass = property.parentDeclaration
                                        ?.closestClassDeclaration()
                                        ?.let {
                                            activityInjectors.put(it, property)
                                        }

                                }


                        }), Unit
                )
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

fun KSClassDeclaration.toFullName(): String {

    return "${this.toPackageName()}.${this.qualifiedName?.getShortName()}"

}

fun KSClassDeclaration.toPackageName(): String {

    return this.qualifiedName?.getQualifier() ?: ""

}