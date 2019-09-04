package org.koin.core.qualifier

/**
 * Class which allows enums to be used as Qualifiers.
 * More strict than strings and are better for refactoring.
 * @author Julian Bell
 * @sample get(named(Direction.NORTH))
 * @sample inject(named(Direction.SOUTH))
 */
data class EnumQualifier<E : Enum<E>>(val value: E) : Qualifier {
    override fun toString(): String {
        return "${value.javaClass.name}.$value"
    }
}
