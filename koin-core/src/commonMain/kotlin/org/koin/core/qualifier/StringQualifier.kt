package org.koin.core.qualifier

data class StringQualifier(override val value: QualifierValue) : Qualifier {
    override fun toString(): String = "'$value'"
}
