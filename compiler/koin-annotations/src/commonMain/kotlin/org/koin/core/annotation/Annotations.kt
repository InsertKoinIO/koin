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
package org.koin.core.annotation

import kotlin.reflect.KClass

/**
 * Koin Annotations
 *
 * @author Arnaud Giuliani
 */

/**
 * Koin definition annotation
 * Declare a type, a function as `single` definition in Koin
 *
 * example:
 *
 * @Single
 * class MyClass(val d : MyDependency)
 *
 * will result in `single { MyClass(get()) }`
 *
 * All dependencies are filled by constructor.
 *
 * @param binds: declared explicit types to bind to this definition. Supertypes are automatically detected
 * @param createdAtStart: create instance at Koin start
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Single(val binds: Array<KClass<*>> = [], val createdAtStart: Boolean = false)

/**
 * same as @Single
 * @see Single
 */
annotation class Singleton(val binds: Array<KClass<*>> = [], val createdAtStart: Boolean = false)

/**
 * Koin definition annotation
 * Declare a type, a function as `factory` definition in Koin
 *
 * example:
 *
 * @Factory
 * class MyClass(val d : MyDependency)
 *
 * will result in `factory { MyClass(get()) }`
 *
 * All dependencies are filled by constructor.
 *
 * @param binds: declared explicit types to bind to this definition. Supertypes are automatically detected
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Factory(val binds: Array<KClass<*>> = [])

/**
 * Declare a class in a Koin scope. Scope name is described by either value (class) or name (string)
 * By default, will declare a `scoped` definition. Can also override with @Scoped, @Factory, @KoinViewModel annotations to add explicit bindings
 *
 * example:
 *
 * @Scope(MyScope::class)
 * class MyClass(val d : MyDependency)
 *
 * will generate:
 * ```
 * scope<MyScope> {
 *  scoped { MyClass(get()) }
 * }
 * ```
 *
 * @param value: scope class value
 * @param name: scope string value
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Scope(val value: KClass<*> = NoClass::class, val name: String = "")
private object NoClass

/**
 * Declare a type, a function as `scoped` definition in Koin. Must be associated with @Scope annotation
 * @see Scope
 *
 * @param binds: declared explicit types to bind to this definition. Supertypes are automatically detected
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Scoped(val binds: Array<KClass<*>> = [])

/**
 * Define a qualifier for a given definition (associated with Koin definition annotation)
 * Will generate `StringQualifier("...")`
 *
 * @param value: string qualifier
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class Named(val value: String)

/**
 * Annotate a constructor parameter or function parameter, to ask resolution as "injected parameter"
 *
 * example:
 *
 * @Factory
 * class MyClass(@InjectedParam val d : MyDependency)
 *
 * will result in `factory { params -> MyClass(params.get()) }`
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class InjectedParam

/**
 * Annotate a constructor parameter or function parameter, to resolve as Koin property
 *
 * example:
 *
 * @Factory
 * class MyClass(@Property("name") val name : String)
 *
 * will result in `factory { MyClass(getProperty("name")) }`
 *
 * @param value: property name
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Property(val value: String)

/**
 * Class annotation, to help gather definitions inside a Koin module.
 * Each function can be annotated with a Koin definition annotation, to declare it
 *
 * example:
 *
 * @Module
 * class MyModule {
 *  @Single
 *  fun myClass(d : MyDependency) = MyClass(d)
 * }
 *
 * will generate:
 *
 * ```
 * val MyModule.module = module {
 *  val moduleInstance = MyModule()
 *  single { moduleInstance.myClass(get()) }
 * }
 *
 *
 * @param includes: Module Classes to include
 */
@Target(AnnotationTarget.CLASS)
annotation class Module(val includes: Array<KClass<*>> = [])

/**
 * Gather definitions declared with Koin definition annotation
 * Will scan in current package or with the explicit package name
 *
 * @param value: package to scan
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
annotation class ComponentScan(val value: String = "")