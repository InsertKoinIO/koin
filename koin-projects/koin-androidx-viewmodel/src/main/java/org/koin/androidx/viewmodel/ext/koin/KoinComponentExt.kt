//package org.koin.androidx.viewmodel.ext.koin
//
//import androidx.lifecycle.ViewModel
//import org.koin.android.viewmodel.ViewModelParameters
//import org.koin.standalone.KoinComponent
//import org.koin.standalone.get
//
///**
// * Create instance with parameters for ViewModelFactory
// * @see implemented ViewModelFactory
// *
// * @param p - ViewModelParameters
// * @param clazz - class
// */
//fun <T : ViewModel> KoinComponent.createInstance(p: ViewModelParameters, clazz: Class<T>): T {
//    // Get params to pass to factory
//    val name = p.name
//    val params = p.parameters
//    // Clear local stuff
//
//    return get(name ?: "", clazz = clazz.kotlin, parameters = params, filter = isViewModel)
//}