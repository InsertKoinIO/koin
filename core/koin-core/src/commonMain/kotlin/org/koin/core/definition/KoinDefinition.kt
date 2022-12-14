package org.koin.core.definition

import org.koin.core.instance.InstanceFactory
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.module.OptionDslMarker

@OptionDslMarker
data class KoinDefinition<R>(val module : Module, val factory : InstanceFactory<R>)