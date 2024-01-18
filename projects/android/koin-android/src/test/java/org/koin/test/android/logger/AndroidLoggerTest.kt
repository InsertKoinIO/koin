package org.koin.test.android.logger

import android.util.Log
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.android.logger.AndroidLogger
import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.times


class AndroidLoggerTest {

    private lateinit var mockedStatic: MockedStatic<Log>

    @Before
    fun init() {
        mockedStatic = Mockito.mockStatic(Log::class.java)
    }

    @After
    fun cleanup() {
        mockedStatic.close()
    }

    @Test
    fun `GIVEN message and level debug WHEN call log THEN log only expected level`() {
        // GIVEN
        val message = "Hello world"
        val level = Level.DEBUG
        val androidLogger = AndroidLogger(level)

        mockedStatic.`when`<Int> { Log.d(KOIN_TAG, message) }.thenReturn(1)

        // WHEN
        androidLogger.log(level, message)

        // THEN
        mockedStatic.verify({ Log.d(KOIN_TAG, message) }, times(1))
        mockedStatic.verify({ Log.i(KOIN_TAG, message) }, times(0))
        mockedStatic.verify({ Log.e(KOIN_TAG, message) }, times(0))
    }

    @Test
    fun `GIVEN message and level info WHEN call log THEN log only expected level`() {
        // GIVEN
        val message = "Hello world"
        val level = Level.INFO
        val androidLogger = AndroidLogger(level)

        mockedStatic.`when`<Int> { Log.i(KOIN_TAG, message) }.thenReturn(2)

        // WHEN
        androidLogger.log(level, message)

        // THEN
        mockedStatic.verify({ Log.d(KOIN_TAG, message) }, times(0))
        mockedStatic.verify({ Log.i(KOIN_TAG, message) }, times(1))
        mockedStatic.verify({ Log.e(KOIN_TAG, message) }, times(0))
    }

    @Test
    fun `GIVEN message and level error WHEN call log THEN log only expected level`() {
        // GIVEN
        val message = "Hello world"
        val level = Level.ERROR
        val androidLogger = AndroidLogger(level)

        mockedStatic.`when`<Int> { Log.e(KOIN_TAG, message) }.thenReturn(3)

        // WHEN
        androidLogger.log(level, message)

        // THEN
        mockedStatic.verify({ Log.d(KOIN_TAG, message) }, times(0))
        mockedStatic.verify({ Log.i(KOIN_TAG, message) }, times(0))
        mockedStatic.verify({ Log.e(KOIN_TAG, message) }, times(1))
    }
}
