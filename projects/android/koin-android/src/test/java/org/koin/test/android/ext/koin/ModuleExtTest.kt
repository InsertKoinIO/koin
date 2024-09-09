package org.koin.test.android.ext.koin

import android.app.Application
import android.content.Context
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test
import org.koin.android.error.MissingAndroidContextException
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

class ModuleExtTest {

    @Test
    fun `GIVEN exception WHEN ty to get android context THEN throws exception`() {
        // GIVEN
        val scope = mockk<Scope>(relaxed = true)
        every { scope.get<Context>() } throws MissingAndroidContextException("message")

        try {
            // WHEN
            scope.androidContext()
        } catch (e: MissingAndroidContextException) {
            // THEN
            Assert.assertEquals("message", e.localizedMessage)
        }
    }

    @Test(expected = MissingAndroidContextException::class)
    fun `GIVEN exception WHEN ty to get android application THEN throws exception`() {
        val scope = mockk<Scope>(relaxed = true)
        every { scope.get<Application>() } throws MissingAndroidContextException("")
        scope.androidApplication()
    }

}
