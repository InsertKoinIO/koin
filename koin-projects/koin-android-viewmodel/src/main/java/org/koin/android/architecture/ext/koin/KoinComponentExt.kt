package org.koin.android.architecture.ext.koin

import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext


/**
 * Resolve an instance by its canonical name
 */
fun <T> KoinComponent.get(modelClass: Class<T>, module: String? = null, parameters: ParameterDefinition): T =
    (StandAloneContext.koinContext as KoinContext).getByTypeName(
        modelClass.canonicalName,
        module,
        parameters
    )

/**
 * Resolve an instance by its canonical name
 */
fun <T> KoinComponent.getByName(name: String, module: String? = null, parameters: ParameterDefinition): T =
    (StandAloneContext.koinContext as KoinContext).getByName(name, module, parameters)