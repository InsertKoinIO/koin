package org.koin.test.android.error

import org.junit.Assert
import org.junit.Test
import org.koin.android.error.MissingAndroidContextException

class MissingAndroidContextExceptionTest {

    @Test
    fun `GIVEN message WHEN check exception THEN get same message`() {
        // GIVEN
        val input = "Hello world"

        // WHEN
        val exception = MissingAndroidContextException(input)

        // THEN
        Assert.assertEquals(input, exception.localizedMessage)
    }
}
