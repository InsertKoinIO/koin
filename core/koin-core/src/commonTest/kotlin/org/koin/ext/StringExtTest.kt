package org.koin.ext

import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtTest {

    @Test
    fun `stripping quotes when only quotes present`() {
        val quotes = "\"\""
        assertEquals("", quotes.clearQuotes())
    }

    @Test
    fun `stripping quotes from an empty string`() {
        val empty = ""
        assertEquals("", empty.clearQuotes())
    }
}
