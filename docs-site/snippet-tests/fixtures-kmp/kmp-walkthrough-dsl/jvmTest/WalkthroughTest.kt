package com.jetbrains.kmpapp

import com.jetbrains.kmpapp.data.InMemoryMuseumStorage
import com.jetbrains.kmpapp.data.MuseumApi
import com.jetbrains.kmpapp.data.MuseumObject
import com.jetbrains.kmpapp.data.MuseumRepository
import com.jetbrains.kmpapp.di.initKoin
import com.jetbrains.kmpapp.platform.PlatformComponent
import com.jetbrains.kmpapp.screens.DetailViewModel
import com.jetbrains.kmpapp.screens.ListViewModel
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatform
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WalkthroughTest {
    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun graphResolves() {
        initKoin()
        val koin = KoinPlatform.getKoin()
        koin.get<MuseumRepository>()
        koin.get<ListViewModel>()
        koin.get<DetailViewModel>()
        assertEquals("JVM", koin.get<PlatformComponent>().getInfo())
    }

    // The claim of the data layer section: without a network, the list stays empty and nothing crashes.
    @Test
    fun failedRequestDoesNotCrash() {
        val uncaught = java.util.concurrent.CopyOnWriteArrayList<Throwable>()
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { _, e -> uncaught.add(e) }
        try {
            MuseumRepository(
                object : MuseumApi {
                    override suspend fun getData(): List<MuseumObject> = throw java.io.IOException("offline")
                },
                InMemoryMuseumStorage(),
            )
            Thread.sleep(500)
            assertEquals(emptyList(), uncaught.toList())
        } finally {
            Thread.setDefaultUncaughtExceptionHandler(previous)
        }
    }
}
