package org.koin.android.architecture.ext.koin

import org.koin.core.KoinContext
import org.koin.core.parameter.Parameters
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext


/**
 * Resolve an instance by its canonical name
 */
fun <T> KoinComponent.get(modelClass: Class<T>, parameters: Parameters): T =
    (StandAloneContext.koinContext as KoinContext).getByTypeName(
        modelClass.canonicalName,
        parameters
    )

/**
 * Resolve an instance by its canonical name
 */
fun <T> KoinComponent.getByName(name: String, parameters: Parameters): T =
    (StandAloneContext.koinContext as KoinContext).getByName(name, parameters)