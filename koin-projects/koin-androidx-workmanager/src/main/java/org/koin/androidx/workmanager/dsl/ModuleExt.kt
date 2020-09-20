package org.koin.androidx.workmanager.dsl

import androidx.work.ListenableWorker
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.bind

/**
 * @author : Fabio de Matos
 * @since : 16/02/2020
 **/
inline fun <reified T : ListenableWorker> Module.worker(
    qualifier: Qualifier = named<T>(),
    override: Boolean = false,
    noinline definition: Definition<T>
): BeanDefinition<*> {
    return factory(qualifier, override, definition).bind<ListenableWorker>()
}
