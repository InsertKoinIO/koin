package org.koin.ext

import kotlin.reflect.KClass

fun KClass<*>.getFullName(): String {
    return classNames[this] ?: saveFullName()
}

private fun KClass<*>.saveFullName(): String {
    val name = this.java.canonicalName
    classNames[this] = name
    return name
}

private val classNames = hashMapOf<KClass<*>, String>()