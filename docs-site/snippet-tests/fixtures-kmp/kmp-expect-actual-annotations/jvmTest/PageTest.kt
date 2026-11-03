package com.example.app

import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PageTest {
    @AfterTest
    fun tearDown() { stopKoin() }

    @Test
    fun graphResolves() {
        val koin = org.koin.plugin.module.dsl.startKoin<PageApp>().koin
        assertEquals("JVM", koin.get<NativeComponent>().getInfo())
    }
}
