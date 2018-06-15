package org.koin.error

/**
 * Error when trying to override with a bean that has not the `allowOverride`
 */
class BeanOverrideException(msg: String) : Exception(msg)