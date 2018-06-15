package org.koin.test.error

/**
 * Definition is Broken (dependencies can't be reached)
 */
class BrokenDefinitionException(msg : String) : Exception(msg)