package com.example.app

import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatform
import kotlin.test.AfterTest
import kotlin.test.Test

class StartKoinTest {
    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun initKoinStartsTheGraph() {
        initKoin()
        KoinPlatform.getKoin().get<UserViewModel>()
    }
}
