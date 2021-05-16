package org.koin.ksp

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import org.koin.core.module.Module
import java.io.OutputStream

/**
 * @author Fabio de Matos
 * @since 21/04/2021.
 */
class FindKoinModulesUseCase(private val logFile: OutputStream?) {

    //    val reference: KSTypeReference
    init {
//        reference = KSTypeReference()
    }

    fun invoke(resolver: Resolver) {

        val visitor = AllFilesVisitor()


        resolver
            .getAllFiles()
            .map {
                it.declarations
            }
            .flatten()
            .map {
                emit("Declarations ${it.containingFile} -- ${it.closestClassDeclaration()}")
            }


        resolver
            .getAllFiles()
            .map {
                it.accept(visitor, Unit)
            }
    }


    fun emit(s: String, indent: String = ".") {

        try {
            logFile?.appendText("FModule $indent$s\n")
        } catch (t: Throwable) {
            println("Cannot write to log")
        }
    }

    inner class AllFilesVisitor() : KSTopDownVisitor<Unit, Unit>() {
        override fun defaultHandler(node: KSNode, data: Unit) {


            when (node) {
                is KSClassDeclaration -> Unit //emit("Nod  is class ${node.asStarProjectedType()} - ${node.toString()}")
                is KSPropertyAccessor -> Unit // emit("Node is proacc ${node.toString()}")
                is KSPropertyDeclaration -> emit("Node is prop2 ${node.type} - ${node.toString()} ${node.typeParameters}")
                is KSFunction -> emit("Function11  ${node.toString()}  ${node.location}}")
                is KSReferenceElement -> emit("Reference ${node.toString()} - ${node.typeArguments} - ${node.location}")
                is KSFunctionDeclaration -> emit("Function2 ${node.toString()}  ${node.closestClassDeclaration()} - ${node.location}}")
            }


            if (node is KSPropertyDeclaration && node.type.toString() == "String") {

//                node.getter?.
                emit("----- ${node.toString()} ${node.getter}")
//                emit ("is Module!  ${node.qualifiedName?.getShortName()} -1 ${node.simpleName} -2 ${node.getter} -3 ${node.location}")
            }

            if (node is KSPropertyDeclaration && node.type.toString() == "Module") {

//                emit ("is Module!  ${node.qualifiedName?.getShortName()} -1 ${node.simpleName} -2 ${node.getter} -3 ${node.location}")
            }

            if (node is KSFunctionDeclaration && node.closestClassDeclaration()
                    .toString() == "Whatever"
            ) {
                emit("Is function ${node.toString()} - ${node.returnType} - ${node.typeParameters} ${node.closestClassDeclaration()}")

                node.declarations
                    .map {

                        node.returnType
                        emit("+++ ${it.toString()} ")
                    }
            }



        }

    }
}