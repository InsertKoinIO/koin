package org.koin.androidx.workmanager.dsl

import androidx.work.ListenableWorker
import org.koin.androidx.workmanager.KoinWorkerFactory
import org.koin.androidx.workmanager.KoinWorkerFactory.Companion.getQualifier
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.ext.getOrCreateScope

/**
 * @author : Fabio de Matos
 * @since : 16/02/2020
 **/
inline fun <reified T : ListenableWorker> Module.worker(
    override: Boolean = false,
    noinline definition: Definition<T>
): BeanDefinition<ListenableWorker> {

    return factory(getQualifier<T>(), override, definition)

}
