///*
// * Copyright 2017-2021 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */

package org.koin.androidx.viewmodel.scope

import android.os.Bundle


/**
 * Scope extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */
//TODO Deprecate for extrasProducer
fun emptyState(): BundleDefinition = { Bundle() }
typealias BundleDefinition = () -> Bundle

//inline fun <reified T : ViewModel> Scope.getViewModel(
//        qualifier: Qualifier? = null,
//        noinline owner: ViewModelOwnerDefinition,
//        noinline parameters: ParametersDefinition? = null,
//): T {
//    return getViewModel(qualifier, owner, T::class, parameters = parameters)
//}
//
//fun <T : ViewModel> Scope.getViewModel(
//        qualifier: Qualifier? = null,
//        owner: ViewModelOwnerDefinition,
//        clazz: KClass<T>,
//        state: BundleDefinition? = null,
//        parameters: ParametersDefinition? = null,
//): T {
//    val ownerDef = owner()
//    return getViewModel(
//            ViewModelParameter(
//                    clazz,
//                    qualifier,
//                    state,
//                    parameters,
//                    ownerDef.store,
//                    ownerDef.stateRegistry
//            )
//    )
//}
//
//internal fun <T : ViewModel> Scope.getViewModel(viewModelParameters: ViewModelParameter<T>): T {
//    val viewModelProvider = ViewModelProvider(viewModelParameters.viewModelStore, pickFactory(viewModelParameters))
//    return viewModelProvider.resolveInstance(viewModelParameters)
//}