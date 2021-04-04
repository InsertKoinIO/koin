package org.koin.ksp

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.visitor.KSTopDownVisitor

class KoinVisitor(
    val ksAnnotated: KSAnnotated,
    val lambda: Function1<KSPropertyDeclaration, Unit>
) : KSTopDownVisitor<Unit, Unit>() {


    override fun defaultHandler(node: KSNode, data: Unit) {

        when (node) {
            is KSPropertyDeclaration -> lambda(node)
        }

    }


}
