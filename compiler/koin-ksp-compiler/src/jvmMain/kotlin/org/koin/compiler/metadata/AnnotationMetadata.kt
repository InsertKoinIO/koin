/*
 * Copyright 2017-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.compiler.metadata

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import org.koin.android.annotation.KoinViewModel
import org.koin.compiler.generator.KoinGenerator.Companion.LOGGER
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
val SCOPED = DefinitionAnnotation("scoped", annotationType = Scoped::class)
val KOIN_VIEWMODEL = DefinitionAnnotation("viewModel", "org.koin.androidx.viewmodel.dsl.viewModel", KoinViewModel::class)

val DEFINITION_ANNOTATION_LIST = listOf(SINGLE, SINGLETON,FACTORY, KOIN_VIEWMODEL, SCOPE, SCOPED)
val DEFINITION_ANNOTATION_LIST_TYPES = DEFINITION_ANNOTATION_LIST.map { it.annotationType }
val DEFINITION_ANNOTATION_LIST_NAMES = DEFINITION_ANNOTATION_LIST.map { it.annotationName?.lowercase(Locale.getDefault()) }

val SCOPE_DEFINITION_ANNOTATION_LIST = listOf(SCOPED, FACTORY, KOIN_VIEWMODEL)
val SCOPE_DEFINITION_ANNOTATION_LIST_NAMES = SCOPE_DEFINITION_ANNOTATION_LIST.map { it.annotationName?.lowercase(Locale.getDefault()) }


fun isValidAnnotation(s: String): Boolean = s.lowercase(Locale.getDefault()) in DEFINITION_ANNOTATION_LIST_NAMES
fun isValidScopeExtraAnnotation(s: String): Boolean = s.lowercase(Locale.getDefault()) in SCOPE_DEFINITION_ANNOTATION_LIST_NAMES
fun isScopeAnnotation(s: String): Boolean = s.lowercase(Locale.getDefault()) == SCOPE.annotationName?.lowercase(Locale.getDefault())

fun getExtraScopeAnnotation(annotations: Map<String, KSAnnotation>): DefinitionAnnotation? {
    val key = annotations.keys.firstOrNull { k -> isValidScopeExtraAnnotation(k) }
    val definitionAnnotation = when (key) {
        SCOPED.annotationName -> SCOPED
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