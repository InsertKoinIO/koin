package org.koin.core.qualifier

data class StringQualifier(val value: String) : Qualifier {
    override fun toString(): String {
        return value
    }
}