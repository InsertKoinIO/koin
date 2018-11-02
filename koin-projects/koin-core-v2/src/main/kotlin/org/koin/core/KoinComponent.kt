package org.koin.core

import org.koin.core.standalone.StandAloneKoinApplication

interface KoinComponent {
    fun getKoin() = StandAloneKoinApplication.get().koin
}

inline fun <reified T> KoinComponent.get(name: String? = null) = getKoin().get<T>(name)
inline fun <reified T> KoinComponent.inject(name: String? = null) = getKoin().inject<T>(name)