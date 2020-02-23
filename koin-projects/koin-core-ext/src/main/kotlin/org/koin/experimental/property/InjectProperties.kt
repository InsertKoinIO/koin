package org.koin.experimental.property

import org.koin.core.Koin
import org.koin.core.context.KoinContextHandler
import org.koin.core.scope.Scope
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty0

fun <T : Any> T.inject(vararg properties: KMutableProperty0<*>) {
    properties.forEach {
        val prop = it as KMutableProperty0<Any>
        val type = prop.returnType.classifier as KClass<*>
        val value = KoinContextHandler.get().get<Any>(type)
        prop.set(value)
    }
}

fun <T : Any> T.inject(koin: Koin, vararg properties: KMutableProperty0<*>) {
    properties.forEach {
        val prop = it as KMutableProperty0<Any>
        val type = prop.returnType.classifier as KClass<*>
        val value = koin.get<Any>(type)
        prop.set(value)
    }
}

fun <T : Any> T.inject(scope: Scope, vararg properties: KMutableProperty0<*>) {
    properties.forEach {
        val prop = it as KMutableProperty0<Any>
        val type = prop.returnType.classifier as KClass<*>
        val value = scope.get<Any>(type)
        prop.set(value)
    }
}