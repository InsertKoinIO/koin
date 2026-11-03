package com.example.app

import org.koin.core.context.stopKoin
import org.koin.core.context.startKoin
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PageTest {
    @AfterTest
    fun tearDown() { stopKoin() }

    @Test
    fun graphResolves() {
        val koin = startKoin { modules(platformModule()) }.koin
        assertEquals("JVM", koin.get<PlatformComponent>().getInfo())
    }
}
