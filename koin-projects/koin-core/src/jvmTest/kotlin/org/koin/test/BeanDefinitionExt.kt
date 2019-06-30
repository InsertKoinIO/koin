package org.koin.test

import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceContext

fun BeanDefinition<*>.hasBeenCreated() = this.instance!!.isCreated(InstanceContext())
