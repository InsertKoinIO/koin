package org.koin.android.architecture.ext

import android.arch.lifecycle.ViewModel
import org.koin.KoinContext
import org.koin.core.bean.Definition
import org.koin.dsl.context.Context
import org.koin.error.NoBeanDefFoundException
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext


/**
 * ViewModel DSL Extension
 * Allow to declare a vieModel - be later inject into Activity/Fragment with dedicated injector
 */
inline fun <reified T : ViewModel> Context.viewModel(name: String = "", noinline definition: Definition<T>) {
    val bean = factory(name, definition)
    bean.bind(ViewModel::class)
}

/**
 * Resolve an instance by its canonical name
 */
fun <T> KoinContext.getByTypeName(canonicalName: String): T {
    val foundDefinitions = beanRegistry.definitions.filter { it.clazz.java.canonicalName == canonicalName }
    return when (foundDefinitions.size) {
        0 -> throw NoBeanDefFoundException("No bean definition found for class name '$canonicalName'")
        1 -> {
            val def = foundDefinitions.first()
            resolveInstance(def.clazz, emptyMap(), { listOf(def) })
        }
        else -> throw NoBeanDefFoundException("Multiple bean definitions found for class name '$canonicalName'")
    }
}

/**
 * Resolve an instance by its canonical name
 */
fun <T> KoinComponent.get(modelClass: Class<T>): T = (StandAloneContext.koinContext as KoinContext).getByTypeName(modelClass.canonicalName)