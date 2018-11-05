package org.koin.test

import org.koin.core.bean.BeanDefinition

fun BeanDefinition<*>.hasBeenCreated() = this.instance.isAlreadyCreated()
