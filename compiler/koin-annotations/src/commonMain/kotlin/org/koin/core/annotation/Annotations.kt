package org.koin.core.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
annotation class Single(val binds: Array<KClass<*>> = [], val createdAtStart: Boolean = false)
annotation class Singleton(val binds: Array<KClass<*>> = [], val createdAtStart: Boolean = false)

@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
annotation class Factory(val binds: Array<KClass<*>> = [])

@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
annotation class Scope(val value: KClass<*> = NoClass::class, val name : String = "")
private object NoClass

@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
annotation class Scoped(val binds: Array<KClass<*>> = [])

@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION,AnnotationTarget.VALUE_PARAMETER)
annotation class Named(val value: String)


@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class InjectedParam()

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Property(val value: String)

@Target(AnnotationTarget.CLASS)
annotation class Module()
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
annotation class ComponentScan(val value: String = "")