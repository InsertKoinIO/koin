package org.koin.test.verify

// Copy pasted from Koin-Annotations - let's see if any reuse later
/**
 *  Annotate a constructor parameter or function parameter, to tag property as "injected parameter" for verification purpose
 *
 * example:
 * class MyClass(@InjectedParam val d : MyDependency)
 *
 * @author Arnaud Giuliani
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class InjectedParam

/**
 * Tag a dependency as already provided by Koin (like DSL declaration, or internals)
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class Provided