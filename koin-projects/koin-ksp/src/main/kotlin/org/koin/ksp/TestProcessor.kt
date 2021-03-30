package org.koin.ksp

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module

import java.io.OutputStream

/**
 * @author Fabio de Matos
 * @since 16/02/2021.
 */
open class TestProcessor : SymbolProcessor {

    lateinit var codeGenerator: CodeGenerator
    lateinit var file: OutputStream
    var invoked = false

    override fun finish() {

    }

    override fun init(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ) {
        this.codeGenerator = codeGenerator

        file = codeGenerator.createNewFile(Dependencies(false), "", "TestProcessor", "log")
//        emit("TestProcessor: init($options)", "")

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

        if (invoked) {
            return emptyList()
        }
        invoked = true

        val moduleKsType = getKsTypeFromClass(resolver, org.koin.core.module.Module::class.java)
        val koinComponentKsType = getKsTypeFromClass(resolver, KoinComponent::class.java)
        val stringKsType = getKsTypeFromClass(resolver, String::class.java)

        resolver
//            .getClassDeclarationByName(Module::class.java.canonicalName)
            .getClassDeclarationByName("org.koin.example.CoffeeApp")
            ?.let {
                emit("MModule??? $it  - ${it.containingFile} - ${it.location} ${it.asStarProjectedType()}", ".")
            }
        resolver
            .getAllFiles()
            .forEach {
                emit("File scanned $it", indent = ".")
            }

        resolver
            .getAllFiles()
            .let { lookupInjects(it) }

        listOf(
            moduleKsType
//            koinComponentKsType,
//            stringKsType
        )
            .forEach { type ->
                resolver.getAllFiles()
                    .let {
                        writeSampleFile(it, type)
                    }
            }



        return listOf()
    }

    private fun lookupInjects(list: List<KSFile>) {

        secondLevelDeclarations(list)
            .map {
                emit(
                    "class ${it.closestClassDeclaration()}  ${it.isDelegated()} Property is $it - type ${it.type} - package ${it.typeParameters} - " +
                            "${it.annotations} -  fff ${it.type.resolve().arguments}" +
                            "-- ${it.getter} -  -  ${it.setter}" +
                            "ppp ${it.findActuals()} - ${it.findExpects()} - ${it.origin}",
                    indent = "____"
                )
            }

    }

    /**
     * Returns all 1st level declarations (class fields) and 2nd level ( fields of class of class?)
     */
    private fun secondLevelDeclarations(list: List<KSFile>): List<KSPropertyDeclaration> {

        val first = list
            .map {
                it.declarations
            }
            .flatten()
            .mapNotNull {
                it as? KSPropertyDeclaration
            }

        val second = list
            .map { it.declarations }
            .flatten()
            .mapNotNull { it as? KSClassDeclaration }
            .map { it.declarations }
            .flatten()
            .mapNotNull {
                it as? KSPropertyDeclaration
            }

        return mutableListOf<KSPropertyDeclaration>()
            .also {
                it.addAll(first)
                it.addAll(second)
            }
    }

    /**
     * Writes one java file by scanning [list] list of files and looking for objects of type [moduleKsType]
     */
    private fun writeSampleFile(
        list: List<KSFile>,
        moduleKsType: KSType?
    ) {

        val fileKt = codeGenerator.createNewFile(Dependencies(false), "", "HELLO", "java")
        fileKt.appendText("public class HELLO{\n")
        fileKt.appendText("public int foo() { return 1234556; }\n")
        fileKt.appendText("public String bar() { return \"whaaaaaat\"; }\n")




        fileKt.appendText("public String barr() { return \"")

        val combined = secondLevelDeclarations(list)
            .filter {
                it.type.resolve().declaration == moduleKsType?.declaration
            }
            .map {
                emit(
                    "Found $it type ${it.type} -- ${it.type.resolve().declaration} from file ${it.containingFile} a // ${moduleKsType?.declaration}",
                    "   +-+-+-  "
                )
                fileKt.appendText("$it ")

                it
            }
            .map {
                it.findActuals()
            }
            .flatten()
            .map {
                emit("What is this $it", "          ---")
            }

        fileKt.appendText("\"; }\n")

        fileKt.appendText("}")
        return

        list
            .map {
                it.declarations
            }
            .flatten()
//            .mapNotNull { it as? KSClassDeclaration }
//            .mapNotNull { it.declarations }
//            .flatten()
            .mapNotNull { it as? KSPropertyDeclaration }
//            .filter { it.type.javaClass.canonicalName == Module::class.java.canonicalName }
            .map {


//                val a = it
//                    .let {
//                        "${it.extensionReceiver} ${it.isDelegated()}  sss ${it.findOverridee()} ${it.findExpects()} ${it.findExpects()} ${it.containingFile}" +
//                                "this --> ${it.closestClassDeclaration()}  that-> ${it.type}  ${it.type.element} -- ${it.typeParameters}" +
//                                "parent decl ${it.parentDeclaration}" +
//                                "\n +++++ ${it.type.resolve()
//                                    .isAssignableFrom(moduleType)} //////////${it.type} - ${it.type.element?.typeArguments} - ${it.type.resolve()}-  ${it.type.javaClass.canonicalName} == ${Module::class.java.canonicalName}"
//                    }

                emit(
                    "Processing ${it} - ${it.javaClass.canonicalName} - ${it.type.resolve()
                        .starProjection()}",
                    "   "
                )
                it
            }
            .joinToString(",")
            .filter {
                true
            }
            .let {
                "public String list = \"$it\";  "
            }
            .let {
                fileKt.appendText(it)
            }
        fileKt.appendText("}")

    }


    fun emit(s: String, indent: String) {
        file.appendText("$indent$s\n")
    }


}


fun OutputStream.appendText(str: String, identation: String = "") {

    this.write((identation + str).toByteArray())
}