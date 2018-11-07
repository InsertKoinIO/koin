package org.koin.ext

import kotlin.reflect.KClass

fun KClass<*>.getName() = this.java.simpleName
fun KClass<*>.getFullName() = this.java.canonicalName
