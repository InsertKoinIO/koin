package org.koin.ksp

import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import kotlin.reflect.KFunction2

/**
 * @author Fabio de Matos
 * @since 06/06/2021.
 */
class KoinVisitor2(private val emit: KFunction2<String, String, Unit>) :
    KSTopDownVisitor<Unit, Unit>() {

    val classSet = mutableSetOf<KSClassDeclaration>()

    override fun defaultHandler(node: KSNode, data: Unit) {


    }

    override fun visitCallableReference(reference: KSCallableReference, data: Unit) {
        super.visitCallableReference(reference, data)

        emit("Callable here $reference", "\t\t")
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)

        emit("Class here $classDeclaration", "\t\t")
        emit("${classDeclaration.annotations}", "\t\t--")

        classDeclaration
            .annotations
            .find {
//            .filter {
                it.annotationType.toString() == KoinInject::class.java.simpleName
            }
            ?.also {
                classSet.add(classDeclaration)
            }
            ?.also {

                emit("${it.toString()} ${it.annotationType} - ${it.arguments}", "\t\t\t")
            }
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        super.visitPropertyDeclaration(property, data)
        emit("Property here $property", "\t\t")
        emit("${property.annotations}", "\t\t--")


    }

    override fun visitAnnotated(annotated: KSAnnotated, data: Unit) {
        super.visitAnnotated(annotated, data)
//        emit("annotated here $annotated", "\t\t")
    }

    override fun visitAnnotation(annotation: KSAnnotation, data: Unit) {
        super.visitAnnotation(annotation, data)

//        emit("annotation here $annotation - ${annotation.arguments}", "\t\t")
    }
}