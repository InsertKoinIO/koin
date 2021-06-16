package org.koin.core

import org.junit.Test
import org.koin.ext.clearQuotes

class StringTest {

    @Test
    fun quoted_strings(){
        assert("test".clearQuotes() == "test")
        assert("te\"st".clearQuotes() == "te\"st")
        assert("\"test\"".clearQuotes() == "test")
        assert("\"\"".clearQuotes() == "")
        assert("\"".clearQuotes() == "\"")
        assert("\"test".clearQuotes() == "\"test")
    }
}