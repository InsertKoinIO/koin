/*
 * Copyright 2017-present the original author or authors.
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
public annotation class Single(val binds: Array<KClass<*>> = [], val createdAtStart: Boolean = false)

/**
 * same as @Single
 * @see Single
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class Singleton(val binds: Array<KClass<*>> = [], val createdAtStart: Boolean = false)

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
public annotation class Factory(val binds: Array<KClass<*>> = [])

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
public annotation class Scope(val value: KClass<*> = Unit::class, val name: String = "")

/**
 * Declare a type, a function as `scoped` definition in Koin. Must be associated with @Scope annotation
 * @see Scope
 *
 * @param binds: declared explicit types to bind to this definition. Supertypes are automatically detected
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class Scoped(val binds: Array<KClass<*>> = [])

/**
 * Annotate a parameter from class constructor or function, to ask resolution for given scope with Scope Id
 *
 * ScopedId can be defined with a String (name parameter) or a type (value parameter)
 *
 * example:
 *
 * @Factory
 * class MyClass(@ScopeId(name = "my_scope_id") val d : MyDependency)
 *
 * will result in `factory { MyClass(getScope("my_scope_id").get()) }`
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class ScopeId(val value: KClass<*> = Unit::class, val name: String = "")

/**
 * Define a qualifier for a given definition (associated with Koin definition annotation)
 * Will generate `StringQualifier("...")`
 *
 * @param value: string qualifier
 * @param type: class qualifier
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
public annotation class Named(val value: String = "", val type: KClass<*> = Unit::class)

/**
 * Define a qualifier for a given definition (associated with Koin definition annotation)
 * Will generate `StringQualifier("...")`
 *
 * @param value: class qualifier
 * @param name: string qualifier
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
public annotation class Qualifier(val value: KClass<*> = Unit::class, val name: String = "")

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
public annotation class Property(val value: String)

/**
 * Annotate a field value that will be Property default value
 *
 * @PropertyValue("name")
 * val defaultName = "MyName"
 *
 * @Factory
 * class MyClass(@Property("name") val name : String)
 *
 * will result in `factory { MyClass(getProperty("name", defaultName)) }`
 */
@Target(AnnotationTarget.FIELD)
public annotation class PropertyValue(val value: String)

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
public annotation class Module(val includes: Array<KClass<*>> = [], val createdAtStart: Boolean = false)

/**
 * Gather definitions declared with Koin definition annotation.
 * Will scan in current package or with the explicit packages names.
 * For scan current package use empty value array or empty string.
 *
 * The [value] parameter supports both exact package names and glob patterns:
 *
 * **Exact package names (without wildcards):**
 * - Example: `com.example.service`
 *    - Scans all classes in the specified package and its subpackages.
 *    - This is equivalent to the glob pattern `com.example**`.
 *
 * **Glob patterns:**
 * 1. Multi-level scan including the root package:
 *    - `com.example**` scans classes in `com.example` as well as in all its subpackages.
 *
 * 2. Multi-level scan excluding the root package:
 *    - `com.example.**` scans only the subpackages of `com.example`, and does not include classes
 *      directly in `com.example`.
 *
 * 3. Single-level wildcard (*):
 *    - Example: `com.example.*.service`
 *       - Matches exactly one level in the package hierarchy.
 *       - For instance, it matches `com.example.user.service` or `com.example.order.service`,
 *         but does NOT match `com.example.service` or `com.example.user.impl.service`.
 *
 * Wildcards can be combined at any level:
 * - Example: `com.**.service.*data`
 *   - Matches any package where, at some level, a "service" subpackage contains a package ending with "data".
 * - Example: `com.*.service.**`
 *   - Scans all classes in any subpackage of a package matching `com.<anything>.service`.
 *
 * @param value The packages to scan. Specify either an exact package name or a glob pattern.
 *              If left empty, the package of the annotated element is used.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
public annotation class ComponentScan(vararg val value: String = [])

/**
 * To be applied on @Module class to be associated to a Configuration
 * - can have several "tags"/flavour
 *
 * Default configuration is by default, default empty name. Else it's named "default".
 *
 * ex:
 *
 * @Module
 * @Configuration
 * class MyModule
 *
 * This indicates that this module will be part of the "default" configuration space
 *
 * You can associate several configurations to a Module
 *
 * @Module
 * @Configuration("prod","test")
 * class MyModule
 *
 * This indicates that this module will be part of the "prod" & "test" configuration space
 *
 * You can also use the "default" space, as well as others:
 *
 * @Module
 * @Configuration("default","test")
 * class MyModule
 *
 * Is available in default configuration, and test.
 *
 * @Module
 * @Configuration("default") is equivalent to @Configuration
 * class MyModule
 *
 *
 * @param value The names of the configurations associated to this module.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
public annotation class Configuration(vararg val value: String = [])

/**
 * Tag a class as a Koin application entry point: helps generate Koin application boostrap, with startKoin or koinApplication function.
 * This also, looks at configurations to scan modules
 *
 * ex:
 *
 * @KoinApplication //this load default configuration
 * MyApp
 *
 * will generate following functions, that will scan for given configurations and included modules:
 * MyApp.startKoin()
 * MyApp.koinApplication()
 *
 * Note that those functions, are open for custom lambda configuration:
 * MyApp.startKoin {
 *  printLogger()
 * }
 *
 * @KoinApplication(
 *  configurations = [ "default","prod" ] // will load "default" & "prod" configurations
 * MyApp
 *
 * @param configurations: list of configuration names to scan
 * @param modules: list of modules to load, besides of configuration
 */
@Target(AnnotationTarget.CLASS)
public annotation class KoinApplication(val configurations: Array<String> = [], val modules : Array<KClass<*>> = [Unit::class])

/**
 * ViewModel annotation for Koin definition
 * Declare a type, a function as `viewModel` definition in Koin
 *
 * example:
 *
 * @KoinViewModel
 * class MyViewModel(val d : MyDependency) : ViewModel()
 *
 * will result in `viewModel { MyViewModel(get()) }`
 *
 * All dependencies are filled by constructor.
 *
 * @param binds: declared explicit types to bind to this definition. Supertypes are automatically detected
 */
@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION)
public annotation class KoinViewModel(val binds: Array<KClass<*>> = [])
