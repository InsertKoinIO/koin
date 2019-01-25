//package org.koin.androidx.viewmodel.builder
//
//import androidx.lifecycle.ViewModel
//import org.koin.androidx.viewmodel.dsl.viewModel
//import org.koin.core.module.Module
//import org.koin.experimental.builder.create
//
///**
// * Create a Factory definition for given type T
// *
// * @param name
// * @param override - allow definition override
// */
//inline fun <reified T : ViewModel> Module.viewModel(
//    name: String? = null,
//    override: Boolean = false
//) {
//    viewModel(name, override) { create<T>() }
//}