/*
 * Copyright 2017-Present the original author or authors.
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
 * Tag a dependency as already provided by Koin (like DSL dynamic declaration, or internals)
 *
 * @author Arnaud Giuliani
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class Provided
