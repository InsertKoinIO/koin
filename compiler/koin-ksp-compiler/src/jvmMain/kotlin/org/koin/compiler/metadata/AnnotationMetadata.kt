package org.koin.compiler.metadata

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
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
val SINGLETON = DefinitionAnnotation("single", annotationType = Singleton::class)
val FACTORY = DefinitionAnnotation("factory", annotationType = Factory::class)
val SCOPE = DefinitionAnnotation("scoped", annotationType = Scope::class)
val KOIN_VIEWMODEL = DefinitionAnnotation("viewModel", "org.koin.androidx.viewmodel.dsl.viewModel", KoinViewModel::class)

val DEFINITION_ANNOTATION_LIST = listOf(SINGLE, SINGLETON,FACTORY, KOIN_VIEWMODEL, SCOPE)
val DEFINITION_ANNOTATION_LIST_TYPES = DEFINITION_ANNOTATION_LIST.map { it.annotationType }
val DEFINITION_ANNOTATION_LIST_NAMES = DEFINITION_ANNOTATION_LIST.map { it.annotationName?.lowercase(Locale.getDefault()) }

val SCOPE_DEFINITION_ANNOTATION_LIST = listOf(FACTORY, KOIN_VIEWMODEL)
val SCOPE_DEFINITION_ANNOTATION_LIST_NAMES = SCOPE_DEFINITION_ANNOTATION_LIST.map { it.annotationName?.lowercase(Locale.getDefault()) }


fun isValidAnnotation(s: String): Boolean = s.lowercase(Locale.getDefault()) in DEFINITION_ANNOTATION_LIST_NAMES
fun isValidScopeExtraAnnotation(s: String): Boolean = s.lowercase(Locale.getDefault()) in SCOPE_DEFINITION_ANNOTATION_LIST_NAMES
fun isScopeAnnotation(s: String): Boolean = s.lowercase(Locale.getDefault()) == SCOPE.annotationName?.lowercase(Locale.getDefault())

fun getExtraScopeAnnotation(annotations: Map<String, KSAnnotation>): DefinitionAnnotation? {
    val key = annotations.keys.firstOrNull { k -> isValidScopeExtraAnnotation(k) }
    val definitionAnnotation = when (key) {
        FACTORY.annotationName -> FACTORY
        KOIN_VIEWMODEL.annotationName -> KOIN_VIEWMODEL
        else -> null
    }
    return definitionAnnotation
}

fun declaredBindings(annotation: KSAnnotation): List<KSDeclaration>? {
    val declaredBindingsTypes = annotation.arguments.firstOrNull { it.name?.asString() == "binds" }?.value as? List<KSType>?
    return declaredBindingsTypes?.map { it.declaration }
}