package org.koin.compiler.metadata

import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.*
import java.util.*
import kotlin.reflect.KClass

data class DefinitionAnnotation(
    val keyword: String,
    val import: String? = null,
    val annotationType: KClass<*>
) {
    val annotationName = annotationType.simpleName
}

val SINGLE = DefinitionAnnotation("single", annotationType = Single::class)
val FACTORY = DefinitionAnnotation("factory", annotationType = Factory::class)
val SCOPE = DefinitionAnnotation("scoped", annotationType = Scope::class)
val KOIN_VIEWMODEL = DefinitionAnnotation("viewModel", "org.koin.androidx.viewmodel.dsl.viewModel", KoinViewModel::class)

val DEFINITION_ANNOTATION_LIST = listOf(SINGLE, FACTORY, KOIN_VIEWMODEL, SCOPE)

val DEFINITION_ANNOTATION_LIST_TYPES = DEFINITION_ANNOTATION_LIST.map { it.annotationType }
val DEFINITION_ANNOTATION_LIST_NAMES = DEFINITION_ANNOTATION_LIST.map { it.annotationName?.lowercase(Locale.getDefault()) }

fun isValidAnnotation(s: String): Boolean = s.lowercase(Locale.getDefault()) in DEFINITION_ANNOTATION_LIST_NAMES
fun isScopeAnnotation(s: String): Boolean = s.lowercase(Locale.getDefault()) == SCOPE.annotationName?.lowercase(Locale.getDefault())

