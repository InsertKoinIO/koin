package org.koin.test

import org.koin.KoinContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext

interface KoinTest : KoinComponent {
}

inline fun <reified T> KoinTest.get(name: String = "") = (StandAloneContext.koinContext as KoinContext).get<T>(name)
