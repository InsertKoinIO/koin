package org.koin.test

import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceContext
import org.koin.core.scope.Scope

fun BeanDefinition<*, *>.hasBeenCreatedIn(scope: Scope) = this.instance!!.isCreated(InstanceContext(scope))
